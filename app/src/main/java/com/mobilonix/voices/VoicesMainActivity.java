package com.mobilonix.voices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.mobilonix.voices.callbacks.Callback;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.notifications.NotificationManager;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.splash.SplashManager;
import com.mobilonix.voices.util.DeeplinkUtil;
import com.mobilonix.voices.util.GeneralUtil;

public class VoicesMainActivity extends AppCompatActivity {
    //implements LocationListener

    //LatLong currentLocation = new LatLong(0, 0);

    private static final int SPLASH_FADE_TIME = 2000;

    public final static String TAG = VoicesMainActivity.class.getCanonicalName();

    public FrameLayout mainContentFrame;
    boolean leaveAppDialogShowing = false;
    WeakHandler handler = new WeakHandler();

    GoogleApiClient googleApiClient;

    Boolean autoLaunchDeepLink = new Boolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voices_main);

        SessionManager.INSTANCE.signIn(new Callback<Boolean>() {
            @Override
            public boolean onExecuted(Boolean data) {
                handleDeeplink(getIntent());
                return false;
            }
        });
        //currentLocation = LocationUtil.getLastLocation(this);

        initViews();
        initialTransition();
    }

    private void initViews() {

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mainContentFrame = (FrameLayout)findViewById(R.id.main_content_frame);
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.primary_toolbar));
        findViewById(R.id.primary_toolbar).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleDeeplink(intent);
    }

    /**
     * The initial app work goes here
     */
    private void initialTransition() {
        SplashManager.INSTANCE.toggleSplashScreen(this, true);
        if(!SessionManager.INSTANCE.checkIfFirstRun(false)) {
            SplashManager.INSTANCE.toggleOnBoardingCopy(false);
            SplashManager.INSTANCE.toggleSplashScreen(VoicesMainActivity.this, false);
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SplashManager.INSTANCE.toggleSplashScreen(VoicesMainActivity.this, false);
                    RepresentativesManager.INSTANCE.toggleRepresentativesScreen(null, VoicesMainActivity.this, true);
                }
            }, SPLASH_FADE_TIME);
        }
    }

    /**
     * This method will handle any incoming deeplinkins
     *
     * @param intent
     */
    public void handleDeeplink(Intent intent) {

        Bundle extras = intent.getExtras();
        if(((extras != null) && extras.containsKey(NotificationManager.NOTIFICATION_KEY))) {
            RepresentativesManager.INSTANCE.selectGroupsTab();
        }

        if(googleApiClient == null) {
           googleApiClient = new GoogleApiClient.Builder(this)
                   .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                       @Override
                       public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                           GeneralUtil.toast("Could not connect to Google API.");
                           Log.e(TAG, "Could not retrieve the deeplink.");
                       }
                   })
                   .addApi(AppInvite.API)
                   .build();
       }


        DeeplinkUtil.parseDeeplink(intent, new Callback<String>() {
            @Override
            public boolean onExecuted(String groupKey) {
                GroupManager.INSTANCE.setDefferredGroupKey(groupKey.toUpperCase(), false);
                autoLaunchDeepLink = true;
                return false;
            }
        });

        autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(googleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    if(deepLink != null) {
                                        deepLink = deepLink.replace("http://tryvoices.com/","");
                                    }
                                    GroupManager.INSTANCE.setDefferredGroupKey(deepLink.toUpperCase(), true);
                                } else {
                                    Log.e(TAG, "getInvitation: no deep link found.");
                                }
                            }
                        });
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
        //if(LocationRequestManager.INSTANCE.isLocationRequestScreenOn()) {
            //if (LocationUtil.isGPSEnabled(this)) {
                //LocationRequestManager.INSTANCE.toggleLocationRequestScreen(this, false);
                //final ProgressDialog progress = ProgressDialog.show(this, "Finding Location",
                        //"Awaiting to resolve location. One moment...", true);

                //progress.setCancelable(false);
                //progress.setCanceledOnTouchOutside(false);

//                LocationUtil.triggerLocationUpdate(this, new Callback<LatLong>() {
//                    @Override
//                    public boolean onExecuted(LatLong data) {
//                        progress.dismiss();
//                        RepresentativesManager.INSTANCE
//                                .toggleRepresentativesScreen(null, VoicesMainActivity.this, true);
//                        return false;
//                    }
//                });
//            }
//        } else {
//            if(!LocationUtil.isGPSEnabled(this) && !SplashManager.INSTANCE.splashScreenVisible) {
//                LocationRequestManager.INSTANCE.showGPSNotEnabledDialog(this);
//            }
//        }
    }

    //@Override
    //protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        /**
         * If the GPS is enabled at this point, then we trigger a location update
         */
//        if(LocationUtil.isGPSEnabled(this) || LocationUtil.isNetworkLocationEnabled(this) ) {
//            LocationRequestManager.INSTANCE.showGPSEnabledDialog(this);
//            LocationUtil.triggerLocationUpdate(this, new Callback<LatLong>() {
//                @Override
//                public boolean onExecuted(LatLong data) {
//                    RepresentativesManager.INSTANCE
//                            .toggleRepresentativesScreen(data,
//                                    VoicesMainActivity.this,
//                                    true);
//                    return false;
//                }
//            });
//        } else {
//            LocationRequestManager.INSTANCE.showGPSNotEnabledDialog(this);
//        }
//
//        LocationRequestManager.INSTANCE.toggleLocationRequestScreen(this, false);
//        RepresentativesManager.INSTANCE
//                .toggleRepresentativesScreen(getCurrentLocation(),
//                this,
//                true);
//    }

    public Toolbar getToolbar() {
        return (Toolbar)findViewById(R.id.primary_toolbar);
    }

    @Override
    protected void onDestroy() {
        //LocationUtil.stopLocationUpdates(this);
        FirebaseAuth.getInstance().signOut();

        super.onDestroy();
    }

    //public LatLong getCurrentLocation() {
        //return currentLocation;
    //}

//    @Override
//    public void onLocationChanged(Location location) {
//        currentLocation = new LatLong(location.getLatitude(), location.getLongitude());
//
//        Callback<LatLong> callback = LocationUtil.getLocationRequestCallback();
//
//        /* Currently we only want this to execute once and then nullify it */
//        if(callback != null) {
//            callback.onExecuted(currentLocation);
//            LocationUtil.setLocationRequestCallback(null);
//        }
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//    @Override
//    public void onProviderEnabled(String provider) {}
//
//    @Override
//    public void onProviderDisabled(String provider) {
//        LocationRequestManager.INSTANCE
//                .toggleLocationRequestScreen(VoicesMainActivity.this, false);
//    }

    public void toggleProgressSpinner(boolean state) {
        findViewById(R.id.app_progress_spinner).setVisibility(state ? View.VISIBLE : View.GONE);
    }

}
