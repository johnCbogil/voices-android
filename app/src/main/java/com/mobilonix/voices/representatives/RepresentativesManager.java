package com.mobilonix.voices.representatives;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.callbacks.Callback;
import com.mobilonix.voices.callbacks.Callback2;
import com.mobilonix.voices.data.api.ApiEngine;
import com.mobilonix.voices.data.api.engines.NycLocalOfficialsApi;
import com.mobilonix.voices.data.api.engines.StateOpenStatesApi;
import com.mobilonix.voices.data.api.engines.UsCongressSunlightApi;
import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.location.model.LatLong;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.model.RepresentativesPage;
import com.mobilonix.voices.representatives.ui.PagerIndicator;
import com.mobilonix.voices.representatives.ui.RepresentativesListAdapter;
import com.mobilonix.voices.representatives.ui.RepresentativesPagerAdapter;
import com.mobilonix.voices.util.GeneralUtil;
import com.mobilonix.voices.util.RESTUtil;
import com.mobilonix.voices.util.ViewUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Request;

//import com.mobilonix.voices.location.model.LatLong;

public enum RepresentativesManager {

    INSTANCE;

    public String TAG = RepresentativesManager.class.getCanonicalName();

    public String CURRENT_LOCATION = "CURRENT_LOCATION";

    static UsCongressSunlightApi sunlightApiEngine = new UsCongressSunlightApi();
    static StateOpenStatesApi openStatesApiEngine = new StateOpenStatesApi();
    static NycLocalOfficialsApi nycScraperApi = new NycLocalOfficialsApi();

    boolean representativesScreenVisible = false;

    String lastActionSelectedForContact = "<NOT COMING FROM GROUP>";
    String groupForLastAction = "<NOT COMING FROM GROUP>";

    FrameLayout representativesFrame;

    View primaryToolbar;

    PagerIndicator pagerIndicator;

    ImageView representativesTabIcon;
    ImageView groupsTabIcon;

    PlaceAutocompleteFragment autoCompleteTextView;
    ConcurrentHashMap<String, ArrayList<Representative>> currentRepsMap = new ConcurrentHashMap<>();

    /**
     *
     * The enum value contains the URL that needs to be called to make the representatives request
     *
     */

    /* Fixme The old enum structure assumed that only an Api URL was necessary to pull data     */
    /* Fixme but every Api response has its own unique JSON signature.                          */

    /* Fixme Currently composing apis in each enum node. I think the nomenclature should change */
    /* Fixme to reflect the fact that there is an ApiEngine intrinsic to "RepresentativesType." */

    public enum RepresentativesType {


        CONGRESS(sunlightApiEngine, "Federal"),
        STATE_LEGISLATORS(openStatesApiEngine, "State"),
        COUNCIL_MEMBERS(nycScraperApi, "Local");

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

        if (state) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            representativesFrame = (FrameLayout)
                    inflater.inflate(R.layout.view_representatives, null, false);

            primaryToolbar = activity.getToolbar();

            final TextView actionSelectionButton = (TextView) primaryToolbar.findViewById(R.id.action_selection_text);
            final TextView groupsSelectionButton = (TextView) primaryToolbar.findViewById(R.id.groups_selection_text);
            final ImageView addGroupIcon = (ImageView)primaryToolbar.findViewById(R.id.action_add_groups);

            primaryToolbar.setVisibility(View.VISIBLE);
            actionSelectionButton.setVisibility(View.GONE);
            groupsSelectionButton.setVisibility(View.GONE);
            addGroupIcon.setVisibility(View.GONE);

            final ArrayList<RepresentativesPage> pages = new ArrayList<>();
            final ViewPager representativesPager = (ViewPager) representativesFrame.findViewById(R.id.representatives_pager);

            /* Initialize Pager Indicator */
            pagerIndicator = ((PagerIndicator) representativesFrame
                    .findViewById(R.id.pager_meta_frame)
                    .findViewById(R.id.pager_indicator));

            for (RepresentativesType representativesType : RepresentativesType.values()) {
                pagerIndicator.addIndicator(representativesType.getIdentifier(), representativesType.getIdentifier());
                pages.add(new RepresentativesPage(new ArrayList<Representative>(), representativesType));
            }
            representativesPager.setAdapter(new RepresentativesPagerAdapter(pages));

            representativesPager.addOnPageChangeListener(pagerIndicator);

            pagerIndicator.addIndicatorShiftCallback(new Callback() {
                @Override
                public boolean onExecuted(Object data) {

                    String tag = "";

                    if(data == null) {
                        tag = pagerIndicator
                                .getCurrentIndicatorTag();
                    } else if (data instanceof String){
                        tag = (String)data;
                    } else if (data instanceof Integer) {
                        representativesPager.setCurrentItem((Integer)data);
                        return true;
                    }


                    ArrayList<RepresentativesType> types = new ArrayList<>();
                    for (RepresentativesType rep : RepresentativesType.values()) {
                        types.add(rep);
                    }

                    ListView listView = ((ListView) representativesFrame
                            .findViewWithTag(tag));

                    ArrayList<Representative> representatives =
                            currentRepsMap.get(tag);

                    if (representatives == null) {
                        representatives = new ArrayList<>();
                    }

                    if ((representatives.size() == 0)) {
                        toggleErrorDisplay(tag, true);
                    }

                    if (listView != null) {
                        listView.setAdapter(
                                new RepresentativesListAdapter(
                                        listView.getContext(),
                                        R.layout.representatives_list_item,
                                        representatives));
                    }

                    return false;
                }
            });

            /* Initialize Autocomplete fragment */
            if (autoCompleteTextView == null) {
                autoCompleteTextView =
                        (PlaceAutocompleteFragment) activity.getFragmentManager()
                                .findFragmentById(R.id.place_autocomplete_fragment);

                autoCompleteTextView.getView().setVisibility(View.VISIBLE);
                autoCompleteTextView.setHint(activity.getString(R.string.search_text_1));
                //autoCompleteTextView.getView().setBackgroundColor(VoicesApplication.getContext().getResources().getColor(R.color.voices_orange));
                autoCompleteTextView.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        refreshRepresentativesContent(
                                place.getAddress().toString(),
                                place.getLatLng().latitude,
                                place.getLatLng().longitude,
                                activity,
                                pages,
                                representativesPager);
                    }
                    @Override
                    public void onError(Status status) {

                    }
                });

                //refreshRepresentativesContent(
                        //CURRENT_LOCATION,
                        //location.getLatitude(),
                        //location.getLongitude(),
                        //activity,
                        //pages,
                        //representativesPager);

                initTabView();

                activity.getMainContentFrame().addView(representativesFrame);
            } else {

                activity.getMainContentFrame().removeView(representativesFrame);
            }

            representativesScreenVisible = state;
        }
    }

    /**
     * Initialize the representatives and view tabs and thir respective actions
     */
    private void initTabView() {

        representativesTabIcon = (ImageView)primaryToolbar.findViewById(R.id.representatives_tab_icon);
        groupsTabIcon = (ImageView)primaryToolbar.findViewById(R.id.groups_tab_icon);

        final ViewPager representativesPager = (ViewPager)representativesFrame.findViewById(R.id.representatives_pager);
        final FrameLayout groupsView = (FrameLayout)representativesFrame.findViewById(R.id.groups_view);

        final TextView actionSelectionButton = (TextView)primaryToolbar.findViewById(R.id.action_selection_text);
        final TextView groupsSelectionButton = (TextView)primaryToolbar.findViewById(R.id.groups_selection_text);
        final ImageView backArrow = (ImageView)primaryToolbar.findViewById(R.id.primary_toolbar_back_arrow);
        final ImageView addGroupIcon = (ImageView)primaryToolbar.findViewById(R.id.action_add_groups);
        //final int voicesOrange = VoicesApplication.getContext().getResources().getColor(R.color.voices_orange);
        //final int grey = VoicesApplication.getContext().getResources().getColor(R.color.grey);

        groupsTabIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                representativesPager.setVisibility(View.GONE);
                groupsView.setVisibility(View.VISIBLE);
                primaryToolbar.setVisibility(View.VISIBLE);
                backArrow.setVisibility(View.GONE);
                actionSelectionButton.setVisibility(View.VISIBLE);
                actionSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.white));
                groupsSelectionButton.setVisibility(View.VISIBLE);
                groupsSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.voices_orange));
                addGroupIcon.setVisibility(View.VISIBLE);

                RepresentativesManager.INSTANCE.toggleSearchBar(false);
                RepresentativesManager.INSTANCE.togglePagerMetaFrame(false);

                //groupsTabIcon.getDrawable().setColorFilter(voicesOrange,PorterDuff.Mode.SRC_ATOP);
                //representativesTabIcon.getDrawable().setColorFilter(grey,PorterDuff.Mode.SRC_ATOP);

                GroupManager.INSTANCE.toggleGroupPage(groupsView, true);
            }
        });

        representativesTabIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Always set the page back to the federal reps when selevting the groups tav*/
                //RepresentativesManager.INSTANCE.setPageByIndex(0);


                representativesPager.setVisibility(View.VISIBLE);
                groupsView.setVisibility(View.GONE);
                primaryToolbar.setVisibility(View.VISIBLE);
                actionSelectionButton.setVisibility(View.GONE);
                groupsSelectionButton.setVisibility(View.GONE);
                backArrow.setVisibility(View.GONE);

                addGroupIcon.setVisibility(View.GONE);

                RepresentativesManager.INSTANCE.toggleSearchBar(true);
                RepresentativesManager.INSTANCE.togglePagerMetaFrame(true);

                //representativesTabIcon.getDrawable().setColorFilter(voicesOrange, PorterDuff.Mode.SRC_ATOP);
                //groupsTabIcon.getDrawable().setColorFilter(grey,PorterDuff.Mode.SRC_ATOP);

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
        addGroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupManager.INSTANCE.toggleGroups(GroupManager.GroupType.ALL);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void selectRepresentativesTab() {
        representativesTabIcon.callOnClick();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void selectGroupsTab() {
        groupsTabIcon.callOnClick();
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
    public void refreshRepresentativesContent(final String locationString,
                                              double repLat,
                                              double repLong,
                                              final VoicesMainActivity activity,
                                              final ArrayList<RepresentativesPage> pages,
                                              final ViewPager representativesPager) {

        for (RepresentativesType type : RepresentativesType.values()) {

            /* reset the error state */
            toggleErrorDisplay(type, false);

            ProgressBar progressSpinner = (ProgressBar)representativesPager
                    .findViewWithTag(type.getIdentifier() + "_PROGRESS");

            if(progressSpinner != null) {
                progressSpinner.setVisibility(View.VISIBLE);
            }


            RESTUtil.makeRepresentativesRequest(repLat, repLong, type,
                    new Callback2<ArrayList<Representative>, RepresentativesType>() {
                @Override
                public boolean onExecuted(final ArrayList<Representative> data, final RepresentativesType type) {

                    //TODO below if statement was put in primarily to handle case when NycLocalOfficialsApi
                    //  is executed outside of NYC. Prefer an a priori way of checking city. Also,
                    //  as more cities are on-boarded, a city API selector will be implemented that
                    //  also requires a priori knowledge

                    activity.getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            ProgressBar progressSpinner = (ProgressBar)representativesPager
                                    .findViewWithTag(type.getIdentifier() + "_PROGRESS");

                            if(progressSpinner != null) {
                                progressSpinner.setVisibility(View.GONE);
                            }
                        }
                    });

                    final ArrayList<Representative> result;
                    if((data == null) || data.isEmpty()) {
                        result = new ArrayList<>();
                    } else{
                        result = data;
                    }

                    activity.getHandler().post(new Runnable() {
                            @Override
                            public void run() {

                                toggleErrorDisplay(type, false);

                                finalizePageOrder(result, type, pages, representativesPager);

                                String location = VoicesApplication.EMPTY;

                                if((result != null) && (result.size() > 0)) {
                                    if (locationString.equals(CURRENT_LOCATION)) {
                                        location = "current location!";
                                    } else {
                                        location = "address: " + locationString;
                                    }
                                    if(pagerIndicator.getCurrentIndicatorTag()
                                            .equals(type.getIdentifier())) {
                                        GeneralUtil.toast("Found "
                                                + type.getIdentifier() + " representatives for " + location);
                                    }
                                } else {
                                    toggleErrorDisplay(type, true);
                                }

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

        ListView representativesListView =
                (ListView) representativesPager
                        .findViewWithTag(type.getIdentifier());
        //SwipeRefreshLayout pageRefresh = (SwipeRefreshLayout)representativesPager
                //.findViewWithTag(type.getIdentifier() + "_REFRESH");


        if (representativesListView != null) {
            representativesListView.setAdapter(
                    new RepresentativesListAdapter(representativesPager.getContext(),
                            R.layout.representatives_list_item, data));
        }

        //if(pageRefresh != null) {
            //pageRefresh.setRefreshing(false);
        //}


    }

    /**
     * Turn the search bar on and off (conditionally)
     *
     * @param state
     */
    public void toggleSearchBar(boolean state) {
        representativesFrame.findViewById(R.id.auto_complete_holder)
                .setVisibility(state ? View.VISIBLE : View.GONE);
//        if(autoCompleteTextView != null) {
//            try {
//                autoCompleteTextView
//                        .getView().setVisibility(state ? View.VISIBLE : View.GONE);
//            } catch (Exception e) {
//                Log.e(TAG, "Auto complete fragment null");
//            }
//        }
    }

    public void setPageByIndex(int index) {

        if(index >= 3) {
            index = 0;
        }

        if((pagerIndicator != null) && (representativesFrame != null)) {
            ((ViewPager) representativesFrame.findViewById(R.id.representatives_pager)).setCurrentItem(index);
            pagerIndicator.onPageSelected(index);
        }
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

    private void toggleErrorDisplay(String identifier, boolean state) {

        ViewPager pager = (ViewPager) representativesFrame
                .findViewById(R.id.representatives_pager);

        LinearLayout errorLayout = (LinearLayout)
                pager.findViewWithTag(identifier + "_ERROR");

        if(errorLayout != null) {
            errorLayout.setVisibility(state ? View.VISIBLE : View.GONE);

            TextView errorMessageText = (TextView)errorLayout.findViewById(R.id.representatives_error_message);

            /* TODO: When we get the local officials available, we'll need to amend this logic */
            if(!identifier.equals(RepresentativesType.COUNCIL_MEMBERS.getIdentifier())) {
                errorMessageText
                        .setText(Html.fromHtml(VoicesApplication.getContext()
                        .getResources()
                        .getString(R.string.reps_fetch_error)
                        .replace("[identifier]","<b>" + identifier + "</b>")));
            } else {
                errorMessageText.setText(R.string.local_not_yet_error);
            }
        }
    }

    public void toggleErrorDisplay(RepresentativesType type, boolean state) {
        toggleErrorDisplay(type.getIdentifier(), state);
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

    public void setLastActionSelectedForContact(String lastActionSelected, String groupForLastAction) {
        this.lastActionSelectedForContact = lastActionSelected;
        this.groupForLastAction = groupForLastAction;
    }

    public String getLastActionSelectedForContact() {
        return lastActionSelectedForContact;
    }

    public String getGroupForLastAction() {
        return groupForLastAction;
    }
}
