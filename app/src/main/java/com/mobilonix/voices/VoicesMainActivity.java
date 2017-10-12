package com.mobilonix.voices;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.firebase.auth.FirebaseAuth;
import com.mobilonix.voices.callbacks.Callback;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.navigation.NavigationAdapter;
import com.mobilonix.voices.navigation.NavigationObject;
import com.mobilonix.voices.notifications.NotificationManager;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.splash.SplashManager;
import com.mobilonix.voices.util.DeeplinkUtil;
import com.mobilonix.voices.util.GeneralUtil;

import java.util.ArrayList;

public class VoicesMainActivity extends AppCompatActivity {

    private ListView navigationList;

    private static final int SPLASH_FADE_TIME = 2000;

    public final static String TAG = VoicesMainActivity.class.getCanonicalName();

    public FrameLayout mainContentFrame;
    boolean leaveAppDialogShowing = false;
    WeakHandler handler = new WeakHandler();

    DrawerLayout drawerLayout;

    GoogleApiClient googleApiClient;

    Boolean autoLaunchDeepLink = new Boolean(false);

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(VoicesApplication.getContext());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voices_main);

        VoicesApplication.setGlobalHandler(new WeakHandler());

        SessionManager.INSTANCE.signIn(new Callback<Boolean>() {
            @Override
            public boolean onExecuted(Boolean data) {
                handleDeeplink(getIntent());
                return false;
            }
        });

        initViews();

        initialTransition();
    }

    private void initViews() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mainContentFrame = (FrameLayout)findViewById(R.id.main_content_frame);
        navigationList = (ListView)findViewById(R.id.drawer_list);
        addDrawerItems();
        setSupportActionBar((android.support.v7.widget.Toolbar) findViewById(R.id.primary_toolbar));
        findViewById(R.id.primary_toolbar).setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleDeeplink(intent);
    }

    //The initial app work goes here
    private void initialTransition() {
        SplashManager.INSTANCE.toggleSplashScreen(this, true, null);
        if(!SessionManager.INSTANCE.isFirstRun(true)) {
            SplashManager.INSTANCE.toggleOnBoardingCopy(false);
            SplashManager.INSTANCE.toggleSplashScreen(VoicesMainActivity.this, false, new Callback<Boolean>() {
                @Override
                public boolean onExecuted(Boolean data) {
                    RepresentativesManager.INSTANCE
                            .toggleRepresentativesScreen(null,
                                    VoicesMainActivity.this, true);
                    return false;
                }
            });
        }
    }

    //This method will handle any incoming deeplinks
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

        if(drawerLayout.isDrawerOpen(GravityCompat.END)){
            drawerLayout.closeDrawer(Gravity.RIGHT);
            return;
        }

        //If we back out too far, we want to make sure the user is ok leaving the app
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
    }

    public Toolbar getToolbar() {
        return (Toolbar)findViewById(R.id.primary_toolbar);
    }

    @Override
    protected void onDestroy() {
        FirebaseAuth.getInstance().signOut();

        super.onDestroy();
    }

    public void toggleProgressSpinner(boolean state) {
        findViewById(R.id.app_progress_spinner).setVisibility(state ? View.VISIBLE : View.GONE);
    }

//  public void callPlaceAutocompleteActivityIntent() {
//        Intent i = new Intent(VoicesMainActivity.this, AutocompleteActivity.class);
//        VoicesMainActivity.this.startActivityForResult(i,1);
//        try {
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException f) {
//            f.printStackTrace();
//        }
//    }

    public void saveAddress(){
        Intent i = new Intent(VoicesMainActivity.this, AutocompleteActivity.class);
        VoicesMainActivity.this.startActivityForResult(i,2);
//        try {
//        } catch (GooglePlayServicesRepairableException e) {
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException f) {
//            f.printStackTrace();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String addressString = data.getStringExtra("address");
                Double latitudeDouble = data.getDoubleExtra("latitude", 38.8976763);
                Double longitudeDouble = data.getDoubleExtra("longitude", -77.0387238);
                RepresentativesManager.INSTANCE.refreshRepresentativesContent(
                        addressString,
                        latitudeDouble,
                        longitudeDouble,
                        this,
                        RepresentativesManager.INSTANCE.getPages(),
                        RepresentativesManager.INSTANCE.getRepresentativesPager());
                GroupManager.INSTANCE.refreshActionDetailReps(addressString, latitudeDouble,
                        longitudeDouble, this, RepresentativesManager.RepresentativesType.CONGRESS);
                final LinearLayout errorLayout = (LinearLayout)findViewById(R.id.layout_error_page);
                errorLayout.setVisibility(View.GONE);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {
            }
        }
        if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String addressString = data.getStringExtra("address");
                Double latitudeDouble = data.getDoubleExtra("latitude", 38.8976763);
                Double longitudeDouble = data.getDoubleExtra("longitude", -77.0387238);
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString("address", addressString);
                edit.putString("lat", Double.toString(latitudeDouble));
                edit.putString("lon", Double.toString(longitudeDouble));
                edit.commit();

                /* Update first row of the Drawer Navigation List with address */
                NavigationAdapter adapter = ((NavigationAdapter)navigationList.getAdapter());
                if(adapter != null) {
                    adapter.updateTopCell(addressString);
                }

                RepresentativesManager.INSTANCE.refreshRepresentativesContent(
                        addressString,
                        latitudeDouble,
                        longitudeDouble,
                        this,
                        RepresentativesManager.INSTANCE.getPages(),
                        RepresentativesManager.INSTANCE.getRepresentativesPager());
                final LinearLayout errorLayout = (LinearLayout)findViewById(R.id.layout_error_page);
                errorLayout.setVisibility(View.GONE);
                GroupManager.INSTANCE.refreshActionDetailReps(addressString, latitudeDouble,
                        longitudeDouble, this, RepresentativesManager.RepresentativesType.CONGRESS);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, status.getStatusMessage());
            } else if (requestCode == RESULT_CANCELED) {
            }
        }
    }
    private void addDrawerItems() {
        final ArrayList<NavigationObject> navigationArray = new ArrayList<NavigationObject>();
        String addressDescription = prefs.getString("address", "");
        if(addressDescription.equals("")){
            navigationArray.add(new NavigationObject("Edit Home Address",
                    "Home not set yet."));
        } else {
            navigationArray.add(new NavigationObject("Edit Home Address", addressDescription));
        }
        navigationArray.add(new NavigationObject("Pro Tips",
                "Make your actions more effective."));
        navigationArray.add(new NavigationObject("Rate App",
                "A higher rating means more people can find the app to support the causes you care about."));
        navigationArray.add(new NavigationObject("Issue Survey",
                "What issues are important to you?"));
        //navigationArray.add(new NavigationObject("Send Feedback",
                //"What could Voices do to better support your causes?"));
        navigationList.setAdapter(new NavigationAdapter(this, R.layout.navigation_item, navigationArray));
        navigationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        saveAddress();
                        navigationArray.get(0).setDescription(prefs.getString("address", ""));
                        break;
                    case 1:
                        final Dialog infoDialog = new Dialog(VoicesMainActivity.this);
                        infoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        infoDialog.setContentView(R.layout.dialog_info);
                        infoDialog.show();
                        TextView infoCloseButton = (TextView) infoDialog.findViewById(R.id.info_close_button);
                        infoCloseButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                infoDialog.dismiss();
                            }
                        });
                        break;
                    case 2:
                        Uri uri = Uri.parse("market://details?id=" + VoicesApplication.getContext().getPackageName());
                        Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, uri);
                        playStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        try {
                            startActivity(playStoreIntent);
                        } catch (ActivityNotFoundException e) {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://play.google.com/store/apps/details?id=" +
                                            VoicesApplication.getContext().getPackageName())));
                        }
                        break;
                    case 3:
                        String url = "https://docs.google.com/forms/d/e/1FAIpQLSfYUFUt9p4rP3tFoI6FN-aXWmX5U7v3eGoI-FZ9ITO9m_IyWQ/viewform";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        break;
                    //case 4:
                        //break;
                    default:
                }

            }
        });
    }

    public void getDrawer(){
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawerLayout.openDrawer(navigationList);
    }

    public boolean locationSaved() {
        if (prefs.getString("address", "").equals("")) {
            return false;
        } else {
            return true;
        }
    }
}