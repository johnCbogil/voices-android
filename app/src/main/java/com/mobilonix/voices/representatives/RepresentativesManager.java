package com.mobilonix.voices.representatives;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.representatives.ui.RepresentativesPagerAdapter;
import com.mobilonix.voices.util.RESTUtil;

import java.util.ArrayList;

public enum RepresentativesManager {

    INSTANCE;

    FrameLayout representativesFrame;

    public void toggleRepresentativesScreen(final VoicesMainActivity activity, boolean state) {
        if(state) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            representativesFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_representatives, null, false);


            final ArrayList<RepresentativesPage> pages = new ArrayList<>();
            final ViewPager representativesPager = (ViewPager)representativesFrame.findViewById(R.id.reprsentatives_pager);

            RESTUtil.makeRepresentativesRequest(new Callback<ArrayList<Representative>>() {
                @Override
                public boolean onExecuted(final ArrayList<Representative> data) {

                    activity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //GeneralUtil.toast("Representatives data: " + data.toString());
                            pages.add(new RepresentativesPage(data));
                            representativesPager.setAdapter(new RepresentativesPagerAdapter(pages));

                        }
                    });

                    return false;
                }
            });

            RESTUtil.makeRepresentativesRequest(new Callback<ArrayList<Representative>>() {
                @Override
                public boolean onExecuted(final ArrayList<Representative> data) {

                    activity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //GeneralUtil.toast("Representatives data: " + data.toString());
                            pages.add(new RepresentativesPage(data));
                            representativesPager.setAdapter(new RepresentativesPagerAdapter(pages));

                        }
                    });

                    return false;
                }
            });

            RESTUtil.makeRepresentativesRequest(new Callback<ArrayList<Representative>>() {
                @Override
                public boolean onExecuted(final ArrayList<Representative> data) {

                    activity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            //GeneralUtil.toast("Representatives data: " + data.toString());
                            pages.add(new RepresentativesPage(data));
                            representativesPager.setAdapter(new RepresentativesPagerAdapter(pages));
                        }
                    });

                    return false;
                }
            });

            activity.getMainContentFrame().addView(representativesFrame);
        } else {
            activity.getMainContentFrame().removeView(representativesFrame);
        }
    }

}
