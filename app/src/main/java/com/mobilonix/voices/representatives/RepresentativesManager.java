package com.mobilonix.voices.representatives;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.location.LocationRequestManager;
import com.mobilonix.voices.location.model.LatLong;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.representatives.ui.RepresentativesPagerAdapter;
import com.mobilonix.voices.util.RESTUtil;

import java.util.ArrayList;

public enum RepresentativesManager {

    INSTANCE;

    FrameLayout representativesFrame;

    /**
     * The enum value contains the URL that needs to be called to make the representatives request
     */
    public enum RepresentativesType {
        CONGRESS("http://www.google.com"),
        STATE_LEGISLATORS("http://www.google.com"),
        COUNCIL_MEMBERS("http://www.google.com");

        private final String url;

        RepresentativesType(String s) {
            url = s;
        }

        public String getUrl() {
            return this.url;
        }
    }

    public void toggleRepresentativesScreen(LatLong location, final VoicesMainActivity activity, boolean state) {
        if(state) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            representativesFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_representatives, null, false);

            final ArrayList<RepresentativesPage> pages = new ArrayList<>();
            final ViewPager representativesPager = (ViewPager)representativesFrame.findViewById(R.id.reprsentatives_pager);

            GeneralUtil.toast("Finding representatives for location LAT: "
                    + location.getLatitude()
                    + ", LONG: "
                    + location.getLongitude());

            refreshRepresentativesContent(location.getLatitude(), location.getLongitude(), activity, pages, representativesPager);

            activity.getMainContentFrame().addView(representativesFrame);
        } else {
            activity.getMainContentFrame().removeView(representativesFrame);
        }
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
}