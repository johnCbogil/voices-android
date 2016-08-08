package com.mobilonix.voices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.badoo.mobile.util.WeakHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.location.LocationRequestManager;
import com.mobilonix.voices.location.model.LatLong;
import com.mobilonix.voices.location.util.LocationUtil;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.splash.SplashManager;

public class VoicesMainActivity extends AppCompatActivity implements LocationListener {

    LatLong currentLocation = new LatLong(0, 0);

    private static final int SPLASH_FADE_TIME = 2000;

    public FrameLayout mainContentFrame;
    boolean leaveAppDialogShowing = false;
    WeakHandler handler = new WeakHandler();

    MenuItem addGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voices_main);

        SessionManager.INSTANCE.signIn();
        currentLocation = LocationUtil.getLastLocation(this);

        initViews();
        initialTransition();

    }

    private void initViews() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mainContentFrame = (FrameLayout)findViewById(R.id.main_content_frame);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.primary_toolbar));
        findViewById(R.id.primary_toolbar).setVisibility(View.GONE);
    }

    /**
     * The initial app work goes here
     */
    private void initialTransition() {

        SplashManager.INSTANCE.toggleSplashScreen(this, true);
        if(!SessionManager.INSTANCE.checkIfFirstRun()) {
            SplashManager.INSTANCE.toggleOnBoardingCopy(false);
            SplashManager.INSTANCE.toggleSplashScreen(VoicesMainActivity.this, false);
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SplashManager.INSTANCE.toggleSplashScreen(VoicesMainActivity.this, false);

                    if (LocationUtil.isGPSEnabled(VoicesMainActivity.this)) {
                        LocationUtil.triggerLocationUpdate(VoicesMainActivity.this, null);
                        RepresentativesManager.INSTANCE
                                .toggleRepresentativesScreen(LocationUtil
                                                .getLastLocation(VoicesMainActivity.this),
                                        VoicesMainActivity.this, true);
                    } else {
                        LocationRequestManager.INSTANCE
                                .toggleLocationRequestScreen(VoicesMainActivity.this, true);

                    }

                }
            }, SPLASH_FADE_TIME);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.primary_menu, menu);

        addGroup = menu.findItem(R.id.action_add_groups);

        return true;
    }

    public FrameLayout getMainContentFrame() {
        return mainContentFrame;
    }

    public WeakHandler getHandler() {

        if (handler == null) {
            handler = new WeakHandler();
        }

        return handler;
    }

    @Override
    public void onBackPressed() {


        /* Since we aren't using fragments, we need to fabricate a back stack.
        * This absolutely OK because it's easy to do, and we get much better
        * control of the back flow than if we were relying on fragment/child
        * fragment lifecycles */
        if(GroupManager.INSTANCE.isGroupPageVisible()) {
            if(GroupManager.INSTANCE.getMODE() == GroupManager.GroupType.ALL) {
                GroupManager.INSTANCE.onBackPress();
                return;
            }
        }

        /* If we back out too far, we want to make sure the user is ok leaving the app */
        if(!leaveAppDialogShowing) {
            showLeaveAppDialog();
        }
    }

    public void showLeaveAppDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Leave Voices");
        builder.setMessage("Are you sure you want to leave the voices app?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveAppDialogShowing = false;
                        dialog.dismiss();
                        finish();
                    }
                }
        );
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveAppDialogShowing = false;
                        dialog.dismiss();
                    }
                }
        );
        Dialog dialog = builder.create();
        leaveAppDialogShowing = true;
        dialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(LocationRequestManager.INSTANCE.isLocationRequestScreenOn()) {
            if (LocationUtil.isGPSEnabled(this)) {
                LocationRequestManager.INSTANCE.toggleLocationRequestScreen(this, false);
                LocationUtil.triggerLocationUpdate(this, null);
                RepresentativesManager.INSTANCE
                        .toggleRepresentativesScreen(LocationUtil.getLastLocation(this), this, true);
            }
        } else {
            if(!LocationUtil.isGPSEnabled(this) && !SplashManager.INSTANCE.splashScreenVisible) {
                LocationRequestManager.INSTANCE.showGPSNotEnabledDialog(this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * If the GPS is enabled at this point, then we trigger a location update
         */
        if(LocationUtil.isGPSEnabled(this) || LocationUtil.isNetworkLocationEnabled(this) ) {
            LocationRequestManager.INSTANCE.showGPSEnabledDialog(this);
            LocationUtil.triggerLocationUpdate(this, null);
        } else {
            LocationRequestManager.INSTANCE.showGPSNotEnabledDialog(this);
        }

        LocationRequestManager.INSTANCE.toggleLocationRequestScreen(this, false);
        RepresentativesManager.INSTANCE
                .toggleRepresentativesScreen(getCurrentLocation(),
                this,
                true);
    }

    public Toolbar getToolbar() {
        return (Toolbar)findViewById(R.id.primary_toolbar);
    }

    public MenuItem getAddGroup() {
        return addGroup;
    }

    @Override
    protected void onDestroy() {
        LocationUtil.stopLocationUpdates(this);
        FirebaseAuth.getInstance().signOut();

        super.onDestroy();
    }

    public void toggleToolbarDivider(boolean state) {
        findViewById(R.id.divider).setVisibility(state ? View.VISIBLE : View.GONE);
    }

    public LatLong getCurrentLocation() {
        return currentLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLong(location.getLatitude(), location.getLongitude());

        GeneralUtil.toast("Location Changed: " + location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onProviderDisabled(String provider) {
        LocationRequestManager.INSTANCE
                .toggleLocationRequestScreen(VoicesMainActivity.this, false);
    }

    public void toggleProgressSpinner(boolean state) {
        findViewById(R.id.app_progress_spinner).setVisibility(state ? View.VISIBLE : View.GONE);
    }

}
