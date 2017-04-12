package com.mobilonix.voices.representatives.ui;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mobilonix.voices.R;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.util.GeneralUtil;

import java.util.ArrayList;

public class RepresentativesPagerAdapter extends PagerAdapter {

    private ArrayList<RepresentativesPage> representatives;

    ArrayList<ViewGroup> pageArray = new ArrayList<>();

    /**
     *
     * @return
     */
    public ArrayList<ViewGroup> getPageArray() {
        return pageArray;
    }

    public RepresentativesPagerAdapter(ArrayList<RepresentativesPage> representatives) {
        this.representatives = representatives;

        //notifyDataSetChanged();
    }

    static int count = 0;

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {

        pageArray.add(collection);

        if(representatives.get(position).getType().getIdentifier()
                .equals(RepresentativesManager.RepresentativesType.CONGRESS) && count > 0) {
            GeneralUtil.toast(RepresentativesManager.RepresentativesType.CONGRESS + "Page created already. returning");
            return null;
        } else {
            GeneralUtil.toast("Count: " + count);
        }

        if(representatives.get(position).getType().getIdentifier().toLowerCase()
                .equals("Federal".toLowerCase())){
            count++;
        } else {
            GeneralUtil.toast("Identifier: " + representatives.get(position).getType().getIdentifier());
        }

        LayoutInflater inflater = LayoutInflater.from(collection.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.reps_page, collection, false);

        final ListView representativesList = (ListView)layout.findViewById(R.id.representatives_list);
        final LinearLayout errorLayout = (LinearLayout)layout.findViewById(R.id.layout_error_page);
        final ProgressBar progressSpinner = (ProgressBar)layout.findViewById(R.id.reps_progress_spinner);

        representativesList.setTag(representatives.get(position).getType().getIdentifier());
        errorLayout.setTag(representatives.get(position).getType().getIdentifier() + "_ERROR");
        progressSpinner.setTag(representatives.get(position).getType().getIdentifier() + "_PROGRESS");

        //errorLayout.setBackgroundColor(ViewUtil.getResourceColor(android.R.color.holo_blue_bright));

        representativesList
                .setAdapter(new RepresentativesListAdapter
                        (representativesList.getContext(),
                                R.layout.reps_item, representatives.get(position).getRepresentatives()));

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
