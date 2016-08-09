package com.mobilonix.voices.representatives.ui;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.badoo.mobile.util.WeakHandler;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.util.RESTUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RepresentativesPagerAdapter extends PagerAdapter {

    private ArrayList<RepresentativesPage> representatives;


    public RepresentativesPagerAdapter(ArrayList<RepresentativesPage> representatives) {
        this.representatives = representatives;

        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {
        LayoutInflater inflater = LayoutInflater.from(collection.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.representatives_page, collection, false);
        //layout.setTag(representatives.get(position).getType().getIdentifier());

        final ListView representativesList = (ListView)layout.findViewById(R.id.representatives_list);
        final SwipeRefreshLayout pageRefresh = (SwipeRefreshLayout)layout.findViewById(R.id.swipe_refresh_layout);

        representativesList.setTag(representatives.get(position).getType().getIdentifier());
        pageRefresh.setTag(representatives.get(position).getType().getIdentifier() + "_REFRESH");

        pageRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                representativesList.setAdapter(new RepresentativesListAdapter
                        (representativesList.getContext(),
                                R.layout.representatives_list_item,
                                new ArrayList<Representative>()));

                RepresentativesManager.INSTANCE
                        .refreshRepresentativesContent(
                                ((VoicesMainActivity) pageRefresh.getContext()).getCurrentLocation().getLatitude(),
                                ((VoicesMainActivity) pageRefresh.getContext()).getCurrentLocation().getLongitude(),
                                ((VoicesMainActivity) pageRefresh.getContext()),
                                representatives,
                                (ViewPager) RepresentativesManager.INSTANCE.getRepresentativesFrame()
                                        .findViewById(R.id.reprsentatives_pager));
            }
        });

        representativesList
                .setAdapter(new RepresentativesListAdapter
                (representativesList.getContext(),
                        R.layout.representatives_list_item, representatives.get(position).getRepresentatives()));


        collection.addView(layout);
        return layout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return representatives.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "REPRESENTATIVES";
    }

}
