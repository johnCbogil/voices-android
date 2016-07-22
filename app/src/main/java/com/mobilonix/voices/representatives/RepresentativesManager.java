package com.mobilonix.voices.representatives;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.engines.UsCongressSunlightApi;
import com.mobilonix.voices.data.api.engines.StateOpenStatesApi;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.location.model.LatLong;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.representatives.ui.RepresentativesPagerAdapter;
import com.mobilonix.voices.util.RESTUtil;
import com.mobilonix.voices.util.ViewUtil;

import java.io.IOException;
import java.util.ArrayList;

public enum RepresentativesManager {

    INSTANCE;

    static UsCongressSunlightApi sunlightApiEngine = new UsCongressSunlightApi();
    static StateOpenStatesApi openStatesApiEngine = new StateOpenStatesApi();

    boolean representativesScreenVisible = false;

    FrameLayout representativesFrame;

    /**
     * The enum value contains the URL that needs to be called to make the representatives request
     */

    /* Fixme The old enum structure assumed that only an Api URL was necessary to pull data     */
    /* Fixme but every Api response has its own unique JSON signature.                          */

    /* Fixme Currently composing apis in each enum node. I think the nomenclature should change */
    /* Fixme to reflect the fact that there is an ApiEngine intrinsic to "RepresentativesType." */

    public enum RepresentativesType {

        CONGRESS(sunlightApiEngine),
        STATE_LEGISLATORS(openStatesApiEngine),
        COUNCIL_MEMBERS(sunlightApiEngine);

        ApiEngine mApi;

        RepresentativesType(ApiEngine a) {
            mApi = a;
        }

        public String getUrl(double lat, double lon) {
            return mApi.generateUrl(lat,lon);
        }

        public ArrayList<Politico> parseJsonResponse(String response) {

            try {
                return mApi.parseData(response);
            } catch (IOException e){
                e.printStackTrace(); //TODO Handle exception with appropriate dialog or activity msg
                return null;
            }
        }
    }

    public void toggleRepresentativesScreen(LatLong location, final VoicesMainActivity activity, boolean state) {


        if(state) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            representativesFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_representatives, null, false);

            final ArrayList<RepresentativesPage> pages = new ArrayList<>();
            final ViewPager representativesPager = (ViewPager)representativesFrame.findViewById(R.id.reprsentatives_pager);

            /* Hide soft keyboard */
            InputMethodManager imm = (InputMethodManager) activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

            GeneralUtil.toast("Finding representatives for location LAT: "
                    + location.getLatitude()
                    + ", LONG: "
                    + location.getLongitude());

            refreshRepresentativesContent(activity.getCurrentLocation().getLatitude(),
                    activity.getCurrentLocation().getLongitude(),
                    activity,
                    pages,
                    representativesPager);

            initTabView();

            activity.getMainContentFrame().addView(representativesFrame);
        } else {
            activity.getMainContentFrame().removeView(representativesFrame);
        }

        representativesScreenVisible = state;
    }

    /**
     * Initialize the representatives and view tabs and thir respective actions
     */
    private void initTabView() {

        final LinearLayout groupsTab = (LinearLayout)representativesFrame.findViewById(R.id.groups_tab);
        final LinearLayout representativesTab = (LinearLayout)representativesFrame.findViewById(R.id.representatives_tab);

        final ViewPager representativesPager = (ViewPager)representativesFrame.findViewById(R.id.reprsentatives_pager);
        final FrameLayout groupsView = (FrameLayout)representativesFrame.findViewById(R.id.groups_view);

        final View primaryToolbar = ((VoicesMainActivity)groupsTab.getContext()).findViewById(R.id.primary_toolbar);

        final TextView actionSelectionButton = (TextView)primaryToolbar.findViewById(R.id.action_selection_text);
        final TextView groupsSelectionButton = (TextView)primaryToolbar.findViewById(R.id.groups_selection_text);

        ViewUtil.setViewColor(representativesTab, android.R.color.holo_blue_light);

        groupsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                representativesPager.setVisibility(View.GONE);
                groupsView.setVisibility(View.VISIBLE);
                primaryToolbar.setVisibility(View.VISIBLE);

                ViewUtil.setViewColor(groupsTab, android.R.color.holo_blue_light);
                ViewUtil.setViewColor(representativesTab, android.R.color.darker_gray);

                GroupManager.INSTANCE.toggleGroupPage(groupsView, true);
            }
        });

        representativesTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                representativesPager.setVisibility(View.VISIBLE);
                groupsView.setVisibility(View.GONE);
                primaryToolbar.setVisibility(View.GONE);

                ViewUtil.setViewColor(groupsTab, android.R.color.darker_gray);
                ViewUtil.setViewColor(representativesTab, android.R.color.holo_blue_light);

                GroupManager.INSTANCE.toggleGroupPage(groupsView, false);
            }
        });

        actionSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSelectionButton.setBackgroundResource(R.drawable.button_back_selected);
                groupsSelectionButton.setBackgroundResource(R.drawable.button_back);

                GroupManager.INSTANCE.toggleActionGroups(true);

            }
        });

        groupsSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSelectionButton.setBackgroundResource(R.drawable.button_back);
                groupsSelectionButton.setBackgroundResource(R.drawable.button_back_selected);

                GroupManager.INSTANCE.toggleActionGroups(false);

            }
        });
    }

    /**
     *
     *
     * @param repLat
     * @param repLong
     * @param activity
     * @param pages
     * @param representativesPager
     */
    public void refreshRepresentativesContent(double repLat,
                                              double repLong,
                                              final VoicesMainActivity activity,
                                              final ArrayList<RepresentativesPage> pages,
                                              final ViewPager representativesPager) {

        for (RepresentativesType type : RepresentativesType.values()) {
            RESTUtil.makeRepresentativesRequest(repLat, repLong, type, new Callback<ArrayList<Representative>>() {
                @Override
                public boolean onExecuted(final ArrayList<Representative> data) {

                    activity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            pages.add(new RepresentativesPage(data));
                            representativesPager.setAdapter(new RepresentativesPagerAdapter(pages));

                        }
                    });

                    return false;
                }
            });
        }
    }

    /**
     * Get a reference to the representatives frame, to make sure that we can deal with pager functions
     * accross the app
     *
     * @return
     */
    public FrameLayout getRepresentativesFrame() {
        return representativesFrame;
    }

    public boolean isRepresentativesScreenVisible() {
        return representativesScreenVisible;
    }

}
