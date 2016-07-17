package com.mobilonix.voices.representatives;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.ApiUtil;
import com.mobilonix.voices.data.api.engines.UsCongressSunlightApi;
import com.mobilonix.voices.data.api.engines.UsOpenStatesApi;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.location.model.LatLong;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.representatives.ui.RepresentativesPagerAdapter;
import com.mobilonix.voices.util.RESTUtil;

import java.io.IOException;
import java.util.ArrayList;

import javax.net.ssl.SSLPeerUnverifiedException;

public enum RepresentativesManager {

    INSTANCE;

    static UsCongressSunlightApi sunlightApiEngine = new UsCongressSunlightApi();
    static UsOpenStatesApi openStatesApiEngine = new UsOpenStatesApi();

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

        ApiUtil mApi;

        RepresentativesType(ApiUtil a) {
            mApi = a;
        }

        public String getUrl(double lat, double lon) {
            return mApi.generateUrl(lat,lon);
        }

        public ArrayList<Politico> parseJsonResponse(String response) {

            try {
                return mApi.parseData(response);
            } catch (IOException e){
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

            GeneralUtil.toast("Finding representatives for location LAT: "
                    + location.getLatitude()
                    + ", LONG: "
                    + location.getLongitude());

//          refreshRepresentativesContent(location.getLatitude(), location.getLongitude(), activity, pages, representativesPager);
            refreshRepresentativesContent(40.758896, -73.985, activity, pages, representativesPager);

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
