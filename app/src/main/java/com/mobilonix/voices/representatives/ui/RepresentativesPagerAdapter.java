package com.mobilonix.voices.representatives.ui;

import android.support.v4.view.PagerAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
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
    }

    @Override
    public Object instantiateItem(ViewGroup collection, final int position) {

        pageArray.add(collection);

        LayoutInflater inflater = LayoutInflater.from(collection.getContext());
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.reps_page, collection, false);

        final ListView representativesList = (ListView)layout.findViewById(R.id.representatives_list);
        final LinearLayout errorLayout = (LinearLayout)layout.findViewById(R.id.layout_error_page);
        final ProgressBar progressSpinner = (ProgressBar)layout.findViewById(R.id.reps_progress_spinner);

        representativesList.setTag(representatives.get(position).getType().getIdentifier());
        errorLayout.setTag(representatives.get(position).getType().getIdentifier() + "_ERROR");
        progressSpinner.setTag(representatives.get(position).getType().getIdentifier() + "_PROGRESS");

        //representativesList
                //.setAdapter(new RepresentativesListAdapter
                        //(representativesList.getContext(),
                                //R.layout.reps_item, representatives.get(position).getRepresentatives()));

        //if(representatives.get(position).getType().getIdentifier().equalsIgnoreCase("Federal")) {
            FrameLayout frameLayout = (FrameLayout) layout.getChildAt(0);
            frameLayout.findViewById(R.id.layout_error_page).setVisibility(View.VISIBLE);
            ImageView repsImage = (ImageView) errorLayout.findViewById(R.id.reps_image);
            final Button addressButton = (Button) errorLayout.findViewById(R.id.address_button);
//            addressButton.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    GeneralUtil.toast("");
//                    return false;
//                }
//            });
        addressButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GeneralUtil.toast("Hello!");
//                    final Dialog addressDialog = new Dialog(VoicesApplication.getContext());
//                    addressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    addressDialog.setContentView(R.layout.dialog_address);
//                    Button okButton = (Button) addressDialog.findViewById(R.id.ok_button);
//                    okButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            addressDialog.dismiss();
//                        }
//                    });
//                    addressDialog.show();
                }
            });
            repsImage.setVisibility(View.GONE);
            ((TextView)frameLayout.findViewById(R.id.representatives_error_message))
                    .setText(Html.fromHtml(VoicesApplication.getContext()
                    .getResources()
                    .getString(R.string.representatives_onboarding)
            ));
        //}

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
