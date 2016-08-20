package com.mobilonix.voices.representatives;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.util.RESTUtil;
import com.mobilonix.voices.util.ViewUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Request;

public enum RepresentativesManager {

    INSTANCE;

    public String TAG = RepresentativesManager.class.getCanonicalName();

    public String CURRENT_LOCATION = "CURRENT_LOCATION";

    static UsCongressSunlightApi sunlightApiEngine = new UsCongressSunlightApi();
    static StateOpenStatesApi openStatesApiEngine = new StateOpenStatesApi();
    static NycCouncilApi nycScraperApi = new NycCouncilApi();

    boolean representativesScreenVisible = false;

    FrameLayout representativesFrame;

    View primaryToolbar;
    View divider;
    LinearLayout groupsTab;
    LinearLayout representativesTab;

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
            divider = activity.findViewById(R.id.divider);

            final TextView actionSelectionButton = (TextView) primaryToolbar.findViewById(R.id.action_selection_text);
            final TextView groupsSelectionButton = (TextView) primaryToolbar.findViewById(R.id.groups_selection_text);
            final MenuItem addGroupButton = ((VoicesMainActivity) primaryToolbar.getContext()).getAddGroup();

            primaryToolbar.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            actionSelectionButton.setVisibility(View.GONE);
            groupsSelectionButton.setVisibility(View.GONE);
            addGroupButton.setVisible(false);

            final TextView representativesTextIndicator = (TextView) primaryToolbar.findViewById(R.id.representatives_type_text);
            representativesTextIndicator.setText(RepresentativesType.CONGRESS.getIdentifier());

            final ArrayList<RepresentativesPage> pages = new ArrayList<>();
            final ViewPager representativesPager = (ViewPager) representativesFrame.findViewById(R.id.representatives_pager);

            /* Initialize Pager Indicator */
            pagerIndicator = ((PagerIndicator) representativesFrame
                    .findViewById(R.id.pager_meta_frame)
                    .findViewById(R.id.pager_indicator));

            for (RepresentativesType representativesType : RepresentativesType.values()) {
                pagerIndicator.addIndicator(representativesType.getIdentifier());
                pages.add(new RepresentativesPage(new ArrayList<Representative>(), representativesType));
            }
            representativesPager.setAdapter(new RepresentativesPagerAdapter(pages));

            representativesPager.addOnPageChangeListener(pagerIndicator);

            pagerIndicator.addIndicatorShiftCallback(new Callback() {
                @Override
                public boolean onExecuted(Object data) {
                    int position = (Integer) data;

                    ArrayList<RepresentativesType> types = new ArrayList<>();
                    for (RepresentativesType rep : RepresentativesType.values()) {
                        types.add(rep);
                    }

                    ListView listView = ((ListView) representativesFrame
                            .findViewWithTag(pagerIndicator
                                    .getCurrentIndicatorTag()));

                    ArrayList<Representative> representatives =
                            currentRepsMap.get(pagerIndicator.getCurrentIndicatorTag());

                    if (representatives == null) {
                        representatives = new ArrayList<>();
                    }

                    if ((representatives.size() == 0)) {
                        toggleErrorDisplay(pagerIndicator.getCurrentIndicatorTag(), true);
                    }

                    if (listView != null) {
                        listView.setAdapter(
                                new RepresentativesListAdapter(
                                        listView.getContext(),
                                        R.layout.representatives_list_item,
                                        representatives));
                    }

                    for (int i = 0; i < types.size(); i++) {
                        if (i == position) {
                            representativesTextIndicator.setText(types.get(i).getIdentifier());
                        }
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
                autoCompleteTextView.setHint(activity.getString(R.string.search_text));
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

                refreshRepresentativesContent(
                        CURRENT_LOCATION,
                        location.getLatitude(),
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
    }

    /**
     * Initialize the representatives and view tabs and thir respective actions
     */
    private void initTabView() {

        groupsTab = (LinearLayout)representativesFrame.findViewById(R.id.groups_tab);
        representativesTab = (LinearLayout)representativesFrame.findViewById(R.id.representatives_tab);

        final ImageView representativesTabIcon = (ImageView)representativesFrame.findViewById(R.id.representatives_tab_icon);
        final ImageView groupsTabIcon = (ImageView)representativesFrame.findViewById(R.id.groups_tab_icon);

        final ViewPager representativesPager = (ViewPager)representativesFrame.findViewById(R.id.representatives_pager);
        final FrameLayout groupsView = (FrameLayout)representativesFrame.findViewById(R.id.groups_view);

        final TextView actionSelectionButton = (TextView)primaryToolbar.findViewById(R.id.action_selection_text);
        final TextView groupsSelectionButton = (TextView)primaryToolbar.findViewById(R.id.groups_selection_text);
        final ImageView infoIcon = (ImageView)primaryToolbar.findViewById(R.id.representatives_info_icon);
        final ImageView helpIcon = (ImageView)primaryToolbar.findViewById(R.id.representatives_help_icon);
        final ImageView backArrow = (ImageView)primaryToolbar.findViewById(R.id.primary_toolbar_back_arrow);
        final TextView groupsInfoText = (TextView)primaryToolbar.findViewById(R.id.all_groups_info_text);
        final TextView representativesTypeText = (TextView)primaryToolbar.findViewById(R.id.representatives_type_text);
        final MenuItem addGroupButton = ((VoicesMainActivity)primaryToolbar.getContext()).getAddGroup();
        final int voicesOrange = VoicesApplication.getContext().getResources().getColor(R.color.voices_orange);
        final int grey = VoicesApplication.getContext().getResources().getColor(R.color.grey);

        helpIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleHelpDialog();
            }
        });

        groupsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                representativesPager.setVisibility(View.GONE);
                groupsView.setVisibility(View.VISIBLE);
                primaryToolbar.setVisibility(View.VISIBLE);
                infoIcon.setVisibility(View.GONE);
                helpIcon.setVisibility(View.GONE);
                backArrow.setVisibility(View.GONE);
                actionSelectionButton.setVisibility(View.VISIBLE);
                actionSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.white));
                groupsSelectionButton.setVisibility(View.VISIBLE);
                groupsSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.voices_orange));
                groupsInfoText.setVisibility(View.GONE);
                representativesTypeText.setVisibility(View.GONE);
                divider.setVisibility(View.VISIBLE);
                addGroupButton.setVisible(true);

                RepresentativesManager.INSTANCE.toggleSearchBar(false);
                RepresentativesManager.INSTANCE.togglePagerMetaFrame(false);

                groupsTabIcon.getDrawable().setColorFilter(voicesOrange,PorterDuff.Mode.SRC_ATOP);
                representativesTabIcon.getDrawable().setColorFilter(grey,PorterDuff.Mode.SRC_ATOP);

                GroupManager.INSTANCE.toggleGroupPage(groupsView, true);
            }
        });

        representativesTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* Always set the page back to the federal reps when selevting the groups tav*/
                RepresentativesManager.INSTANCE.setPageByIndex(0);


                representativesPager.setVisibility(View.VISIBLE);
                groupsView.setVisibility(View.GONE);
                primaryToolbar.setVisibility(View.VISIBLE);
                infoIcon.setVisibility(View.VISIBLE);
                helpIcon.setVisibility(View.VISIBLE);
                actionSelectionButton.setVisibility(View.GONE);
                groupsSelectionButton.setVisibility(View.GONE);
                groupsInfoText.setVisibility(View.GONE);
                backArrow.setVisibility(View.GONE);
                divider.setVisibility(View.VISIBLE);
                representativesTypeText.setVisibility(View.VISIBLE);

                addGroupButton.setVisible(false);

                RepresentativesManager.INSTANCE.toggleSearchBar(true);
                RepresentativesManager.INSTANCE.togglePagerMetaFrame(true);

                representativesTabIcon.getDrawable().setColorFilter(voicesOrange, PorterDuff.Mode.SRC_ATOP);
                groupsTabIcon.getDrawable().setColorFilter(grey,PorterDuff.Mode.SRC_ATOP);

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

        if(SessionManager.INSTANCE.checkIfFirstRun(true)) {
           toggleHelpDialog();
        }

    }

    public void toggleHelpDialog() {

        final Dialog helpDialog;

        helpDialog = new Dialog(representativesTab.getContext());
        helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        helpDialog.setContentView(R.layout.dialog_instructions);
        helpDialog.setTitle(R.string.instructions_title);
        Button gotItButton = (Button)helpDialog.findViewById(R.id.got_it_button);
        gotItButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpDialog.dismiss();
            }
        });
        helpDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public void selectRepresentativesTab() {
        representativesTab.callOnClick();
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

                    //TODO below if statement was put in primarily to handle case when NycCouncilApi
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
        if((pagerIndicator != null) && (representativesFrame != null)) {
            ((ViewPager) representativesFrame.findViewById(R.id.representatives_pager)).setCurrentItem(0);
            pagerIndicator.onPageSelected(0);
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

            TextView errorHintText = (TextView)errorLayout.findViewById(R.id.reps_page_error_hint);
            TextView errorMessageText = (TextView)errorLayout.findViewById(R.id.representatives_error_message);

            /* TODO: When we get the local officials available, we'll need to amend this logic */
            if(!identifier.equals(RepresentativesType.COUNCIL_MEMBERS.getIdentifier())) {
                errorMessageText
                        .setText(Html.fromHtml(VoicesApplication.getContext()
                        .getResources()
                        .getString(R.string.reps_fetch_error)
                        .replace("[identifier]","<b>" + identifier + "</b>")));

                errorHintText.setText(Html.fromHtml("<b>Hint:</b> "
                        + errorHintText.getText().toString().replace("Hint:", "")));
            } else {
                errorMessageText.setText(R.string.council_not_yet_error);
                errorHintText.setText(VoicesApplication.EMPTY);
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

}
