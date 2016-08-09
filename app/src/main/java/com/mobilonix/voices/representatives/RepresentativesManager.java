package com.mobilonix.voices.representatives;

import android.content.Context;
import android.support.v4.view.ViewPager;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.engines.NycCouncilApi;
import com.mobilonix.voices.data.api.engines.StateOpenStatesApi;
import com.mobilonix.voices.data.api.engines.UsCongressSunlightApi;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.delegates.Callback2;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.location.model.LatLong;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.representatives.ui.PagerIndicator;
import com.mobilonix.voices.representatives.ui.RepresentativesListAdapter;
import com.mobilonix.voices.representatives.ui.RepresentativesPagerAdapter;
import com.mobilonix.voices.util.RESTUtil;
import com.mobilonix.voices.util.ViewUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Request;

public enum RepresentativesManager {

    INSTANCE;

    public String TAG = RepresentativesManager.class.getCanonicalName();

    static UsCongressSunlightApi sunlightApiEngine = new UsCongressSunlightApi();
    static StateOpenStatesApi openStatesApiEngine = new StateOpenStatesApi();
    static NycCouncilApi nycScraperApi = new NycCouncilApi();

    boolean representativesScreenVisible = false;

    FrameLayout representativesFrame;

    PagerIndicator pagerIndicator;

    PlaceAutocompleteFragment autoCompleteTextView;
    ConcurrentHashMap<String, ArrayList<Representative>> currentRepsMap = new ConcurrentHashMap<>();

    /**
     * The enum value contains the URL that needs to be called to make the representatives request
     */

    /* Fixme The old enum structure assumed that only an Api URL was necessary to pull data     */
    /* Fixme but every Api response has its own unique JSON signature.                          */

    /* Fixme Currently composing apis in each enum node. I think the nomenclature should change */
    /* Fixme to reflect the fact that there is an ApiEngine intrinsic to "RepresentativesType." */

    public enum RepresentativesType {


        CONGRESS(sunlightApiEngine, "Federal"),
        STATE_LEGISLATORS(openStatesApiEngine, "State"),
        COUNCIL_MEMBERS(nycScraperApi, "City");

        ApiEngine mApi;
        String identifier;

        RepresentativesType(ApiEngine a, String identifier) {
            mApi = a;
            this.identifier = identifier;
        }

        //TODO have this throw an error on API failure rather than network failure
        public Request getRequest(double lat, double lon) throws IOException {

            Request a = mApi.generateRequest(lat, lon);

            if(a == null) {
                throw new IOException("API Failure");
            }

            return a;
        }


        public ArrayList<Politico> parseJsonResponse(String response) {

            try {
                return mApi.parseData(response);
            } catch (IOException e){
                e.printStackTrace(); //TODO Handle exception with appropriate dialog or activity msg
                return null;
            }
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    public void toggleRepresentativesScreen(LatLong location, final VoicesMainActivity activity, boolean state) {

        if(state) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            representativesFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_representatives, null, false);

            final TextView representativesTextIndicator = (TextView)representativesFrame.findViewById(R.id.representatives_type_text);
            representativesTextIndicator.setText(RepresentativesType.CONGRESS.getIdentifier());

            final ArrayList<RepresentativesPage> pages = new ArrayList<>();
            final ViewPager representativesPager = (ViewPager)representativesFrame.findViewById(R.id.reprsentatives_pager);

            /* Initialize Pager Indicator */
            pagerIndicator = ((PagerIndicator)representativesFrame
                    .findViewById(R.id.pager_meta_frame)
                    .findViewById(R.id.pager_indicator));

            for(RepresentativesType representativesType : RepresentativesType.values()) {
                pagerIndicator.addIndicator(representativesType.getIdentifier());
                pages.add(new RepresentativesPage(new ArrayList<Representative>(), representativesType));
            }
            representativesPager.setAdapter(new RepresentativesPagerAdapter(pages));

            representativesPager.addOnPageChangeListener(pagerIndicator);

            pagerIndicator.addIndicatorShiftCallback(new Callback() {
                @Override
                public boolean onExecuted(Object data) {
                    int position = (Integer)data;

                    ArrayList<RepresentativesType> types = new ArrayList<>();
                    for (RepresentativesType rep : RepresentativesType.values()) {
                        types.add(rep);
                    }

                    for (int i = 0; i < types.size(); i++) {
                        if(i == position) {
                            representativesTextIndicator.setText(types.get(i).getIdentifier());
                        }
                    }

                    ListView listView = ((ListView)representativesFrame
                            .findViewWithTag(pagerIndicator
                                    .getCurrentIndicatorTag()));

                    if(listView != null) {
                        listView.setAdapter(
                                new RepresentativesListAdapter(
                                        listView.getContext(),
                                        R.layout.representatives_list_item,
                                        currentRepsMap.get(pagerIndicator.getCurrentIndicatorTag())));
                    }

                    return false;
                }
            });

            /* Initialize Autocomplete fragment */
            autoCompleteTextView =
                    (PlaceAutocompleteFragment) activity.getFragmentManager()
                            .findFragmentById(R.id.place_autocomplete_fragment);

            if(autoCompleteTextView != null) {
                autoCompleteTextView.getView().setVisibility(View.VISIBLE);
                autoCompleteTextView.setHint(activity.getString(R.string.search_text));
                autoCompleteTextView.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        GeneralUtil.toast("Search for place");
                    }

                    @Override
                    public void onError(Status status) {

                    }
                });
            }

            refreshRepresentativesContent(location.getLatitude(),
                    location.getLongitude(),
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
                RepresentativesManager.INSTANCE.toggleSearchBar(false);
                RepresentativesManager.INSTANCE.togglePagerMetaFrame(false);


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

                RepresentativesManager.INSTANCE.toggleSearchBar(true);
                RepresentativesManager.INSTANCE.togglePagerMetaFrame(true);

                ViewUtil.setViewColor(groupsTab, android.R.color.darker_gray);
                ViewUtil.setViewColor(representativesTab, android.R.color.holo_blue_light);

                GroupManager.INSTANCE.toggleGroupPage(groupsView, false);
            }
        });

        actionSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                actionSelectionButton.setBackgroundResource(R.drawable.button_back_selected);
                actionSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.white));

                groupsSelectionButton.setBackgroundResource(R.drawable.button_back);
                groupsSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.voices_orange));

                GroupManager.INSTANCE.toggleGroups(GroupManager.GroupType.ACTION);

            }
        });

        groupsSelectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionSelectionButton.setBackgroundResource(R.drawable.button_back);
                actionSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.voices_orange));

                groupsSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.white));
                groupsSelectionButton.setBackgroundResource(R.drawable.button_back_selected);

                GroupManager.INSTANCE.toggleGroups(GroupManager.GroupType.USER);

            }
        });

        /* Show all the groups */
        ((VoicesMainActivity)groupsTab.getContext()).getAddGroup()
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        GroupManager.INSTANCE.toggleGroups(GroupManager.GroupType.ALL);

                        return false;
                    }
                });

    }

    /**
     *
     *
     * @param repLat
     * @param repLong
     * @param activity;
     * @param pages
     * @param representativesPager
     */
    public void refreshRepresentativesContent(double repLat,
                                              double repLong,
                                              final VoicesMainActivity activity,
                                              final ArrayList<RepresentativesPage> pages,
                                              final ViewPager representativesPager) {

//      Below is used to test officials for a specific lat / lon
//          Should return district 3 Corey Johnson
//        repLat = 40.74493027;
//        repLong = -73.99040485;

//      Below is used to test an address that will fail
//        repLat = 40.76404572;
//        repLong = -73.88193512;

        activity.toggleProgressSpinner(true);

        for (RepresentativesType type : RepresentativesType.values()) {
            RESTUtil.makeRepresentativesRequest(repLat, repLong, type,
                    new Callback2<ArrayList<Representative>, RepresentativesType>() {
                @Override
                public boolean onExecuted(final ArrayList<Representative> data, final RepresentativesType type) {

                    //TODO below if statement was put in primarily to handle case when NycCouncilApi
                    //  is executed outside of NYC. Prefer an a priori way of checking city. Also,
                    //  as more cities are on-boarded, a city API selector will be implemented that
                    //  also requires a priori knowledge

                    final ArrayList<Representative> result;
                    if((data == null) || data.isEmpty()) {
                        result = new ArrayList<>();
                    } else{
                        result = data;
                    }

                    activity.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                finalizePageOrder(result, type, pages, representativesPager);
                            }
                    });

                    return false;
                }
            });
        }
    }

    private void finalizePageOrder(ArrayList<Representative> data,
                                   RepresentativesType type,
                                   ArrayList<RepresentativesPage> pages,
                                   ViewPager representativesPager) {

        currentRepsMap.put(type.getIdentifier(), data);

        GeneralUtil.toast("Got new representatives for current location!" );

        ListView representativesListView =
                (ListView) representativesPager
                        .findViewWithTag(type.getIdentifier());
        SwipeRefreshLayout pageRefresh = (SwipeRefreshLayout)representativesPager
                .findViewWithTag(type.getIdentifier() + "_REFRESH");

        if (representativesListView != null) {
            representativesListView.setAdapter(
                    new RepresentativesListAdapter(representativesPager.getContext(),
                            R.layout.representatives_list_item, data));
        }

        if(pageRefresh != null) {
            pageRefresh.setRefreshing(false);
        }

        if(pages.size() == RepresentativesType.values().length) {
            ((VoicesMainActivity)representativesPager.getContext()).toggleProgressSpinner(false);
        }

    }

    /**
     * Turn the search bar on and off (conditionally)
     *
     * @param state
     */
    public void toggleSearchBar(boolean state) {
        autoCompleteTextView
                .getView().setVisibility(state ? View.VISIBLE : View.GONE);
    }

    /**
     * Turn the meta section on and off (conditionally)
     *
     * @param state
     */
    public void togglePagerMetaFrame(boolean state) {
        representativesFrame.findViewById(R.id.pager_meta_frame)
                .setVisibility(state ? View.VISIBLE : View.GONE);
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
