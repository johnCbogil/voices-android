package com.mobilonix.voices.groups;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.badoo.mobile.util.WeakHandler;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.analytics.AnalyticsManager;
import com.mobilonix.voices.callbacks.Callback;
import com.mobilonix.voices.callbacks.Callback2;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.ui.EntityContainer;
import com.mobilonix.voices.groups.ui.GroupPage;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.ui.RepresentativesListAdapter;
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.util.AvenirBoldTextView;
import com.mobilonix.voices.util.AvenirTextView;
import com.mobilonix.voices.util.RESTUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.mobilonix.voices.R.string.share;

public enum GroupManager {

    INSTANCE;

    ViewGroup pageRoot;

    boolean isRefreshing = false;

    boolean subscriptionCompleted = false;


    private final String TAG = GroupManager.class.getCanonicalName();

    GroupDetailContainer gc;

    GroupPage groupPage;

    Toolbar mainTB;

    GroupType MODE;

    RelativeLayout actionDetails;

    public enum GroupType {
        ACTION,
        USER,
        ALL,
        ACTION_DETAIL
    }

    boolean groupPageVisible = false;

    ArrayList<Group> allGroupsData = new ArrayList<>();
    ArrayList<Action> allActions= new ArrayList<>();

    public void setAllActions(ArrayList<Action> allActions) {
        this.allActions = allActions;
    }

    String deferredGroupKey = null;

    boolean isExpanded1;
    boolean isExpanded2;

    public void toggleGroupPage(ViewGroup pageRoot, boolean state) {
        this.pageRoot = pageRoot;
        if (isRefreshing) {
            ((VoicesMainActivity) pageRoot.getContext()).toggleProgressSpinner(true);
        } else {
            ((VoicesMainActivity) pageRoot.getContext()).toggleProgressSpinner(false);
        }

        if (state) {
            if (groupPage == null) {
                LayoutInflater inflater = (LayoutInflater) pageRoot.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                groupPage = (GroupPage) inflater.inflate(R.layout.view_groups_screen, null, false);
            }

            /* Add the groups view to the main page*/
            if (groupPage.getParent() != null)
                ((ViewGroup) groupPage.getParent()).removeView(groupPage);

            pageRoot.addView(groupPage);

            /* TODO: Make a request here via asynchronous callback to load the actual group data*/
            /* TODO: We want to retrieve this from cache first, otherwise if not present, re-request it from backend */
            refreshGroupsAndActionList();

            toggleGroups(GroupType.ACTION);

            groupPageVisible = true;
        } else {
            if (groupPage != null) {

                ((VoicesMainActivity) pageRoot.getContext()).toggleProgressSpinner(false);
                pageRoot.removeView(groupPage);
            }

            groupPageVisible = false;
        }
    }

    public void refreshGroupsAndActionList() {
        /* TODO: Make a request here via asynchronous callback to load the actual group data*/
        /* TODO: We wanto retrieve this from cache first, otherwise if not present, re-request it from backend */

        isRefreshing = true;
        ((VoicesMainActivity) groupPage.getContext())
                .toggleProgressSpinner(isRefreshing);

        SessionManager.INSTANCE.fetchAllGroupsFromDatabase(new Callback<ArrayList<Group>>() {

            @Override
            public boolean onExecuted(ArrayList<Group> data) {
                allGroupsData = data;
                groupPage.setAllGroups(data);
                return false;
            }
        }, new Callback<ArrayList<Group>>() {
            @Override
            public boolean onExecuted(ArrayList<Group> data) {

                groupPage.setUserGroups(data);

                SessionManager.INSTANCE.fetchAllActions(new Callback<ArrayList<Action>>() {
                    @Override
                    public boolean onExecuted(ArrayList<Action> data) {

                        groupPage.setActions(data);
                        allActions = data;
                        isRefreshing = false;
                        ((VoicesMainActivity) groupPage.getContext())
                                .toggleProgressSpinner(isRefreshing);

                        return false;
                    }
                });

                if (deferredGroupKey != null) {
                    subscribeToGroup(findGroupWithKey(deferredGroupKey), true, null);
                    deferredGroupKey = null;
                }

                return false;
            }
        });
    }



    public void toggleGroups(GroupType groupType) {

        mainTB = ((VoicesMainActivity) groupPage.getContext()).getToolbar();

        int indicatorBlue = VoicesApplication.getContext()
                .getResources().getColor(R.color.indicator_blue);
        int indicatorGrey = VoicesApplication.getContext()
                .getResources().getColor(R.color.indicator_grey);
        mainTB.findViewById(R.id.toolbar_previous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPress();
            }
        });
        if (groupType == GroupType.ACTION) {
            groupPage.findViewById(R.id.actions_container).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.actions_details_container).setVisibility(View.GONE);
            mainTB.findViewById(R.id.hamburger_icon).setVisibility(View.VISIBLE);

            mainTB.setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_reps).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_groups).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.groups_horizontal).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.reps_horizontal).setVisibility(View.INVISIBLE);
            mainTB.findViewById(R.id.takeaction).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_previous).setVisibility(View.GONE);
            mainTB.findViewById(R.id.allgroups_text).setVisibility(View.GONE);

            ((EntityContainer) groupPage.findViewById(R.id.actions_container)).setType(groupType);
            mainTB.findViewById(R.id.toolbar_add).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_add_linear_layout).setVisibility(View.VISIBLE);

            ((AvenirBoldTextView)(groupPage.findViewById(R.id.actions_container))
                    .findViewById(R.id.actions_button)).setTextColor(indicatorBlue);
            ((AvenirBoldTextView)(groupPage.findViewById(R.id.actions_container))
                    .findViewById(R.id.groups_button)).setTextColor(indicatorGrey);
            MODE = GroupType.ACTION;

        } else if (groupType == GroupType.USER) {
            mainTB.findViewById(R.id.hamburger_icon).setVisibility(View.VISIBLE);

            groupPage.findViewById(R.id.actions_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.actions_details_container).setVisibility(View.GONE);

            mainTB.setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_previous).setVisibility(View.GONE);
            mainTB.findViewById(R.id.toolbar_reps).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_groups).setVisibility(View.VISIBLE);

            ((EntityContainer) groupPage.findViewById(R.id.user_groups_container)).setType(groupType);

            mainTB.findViewById(R.id.toolbar_add).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_add_linear_layout).setVisibility(View.VISIBLE);

            MODE = GroupType.USER;

            ((AvenirBoldTextView)(groupPage.findViewById(R.id.user_groups_container))
                    .findViewById(R.id.actions_button)).setTextColor(indicatorGrey);
            ((AvenirBoldTextView)(groupPage.findViewById(R.id.user_groups_container))
                    .findViewById(R.id.groups_button)).setTextColor(indicatorBlue);

        } else if (groupType == GroupType.ALL) {
            groupPage.findViewById(R.id.actions_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.actions_details_container).setVisibility(View.GONE);

            ((EntityContainer) groupPage.findViewById(R.id.all_groups_container)).setType(groupType);

            mainTB.findViewById(R.id.toolbar_previous).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.allgroups_text).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.toolbar_add).setVisibility(View.GONE);
            mainTB.findViewById(R.id.toolbar_add_linear_layout).setVisibility(View.GONE);
            mainTB.findViewById(R.id.toolbar_reps).setVisibility(View.GONE);
            mainTB.findViewById(R.id.toolbar_groups).setVisibility(View.GONE);
            mainTB.findViewById(R.id.groups_horizontal).setVisibility(View.GONE);
            mainTB.findViewById(R.id.hamburger_icon).setVisibility(View.GONE);


            ((AvenirBoldTextView)(groupPage.findViewById(R.id.all_groups_container))
                    .findViewById(R.id.actions_button)).setTextColor(indicatorGrey);
            ((AvenirBoldTextView)(groupPage.findViewById(R.id.all_groups_container))
                    .findViewById(R.id.groups_button)).setTextColor(indicatorBlue);
            MODE = GroupType.ALL;
        } else if (groupType == GroupType.ACTION_DETAIL) {
            groupPage.findViewById(R.id.actions_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.actions_details_container).setVisibility(View.VISIBLE);
            ((EntityContainer) groupPage.findViewById(R.id.user_groups_container)).setType(groupType);
            mainTB.setVisibility(View.GONE);
            MODE = GroupType.ACTION_DETAIL;
        }
    }

    /**
     * This is a quick way to test if group subscriptions are working
     *
     * @param group
     */


    public void goToGroupDetailPage(final Group group) {
        if (group.getActions() == null) return;
        //this tracks what the last screen was that we were looking at, before going to the group page
        GroupType temp = MODE;
        //we need to add group page behind it because without it, we get a cropped view; this is a workaround to a bug
        toggleGroupPage(pageRoot,true);
        MODE = temp;
        LayoutInflater inflater = (LayoutInflater) pageRoot.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        if (gc == null) {
            gc = (GroupDetailContainer) inflater.inflate(R.layout.group_detail, null, false);
        }
        //if a user clicks on a group too quickly, some of these calls will be incomplete, so we catch the exception
        try {
            gc.setBack(mainTB.findViewById(R.id.toolbar_previous));
            gc.setUserGroups(allGroupsData);
            gc.setGroup(group);
            gc.setActions(allActions);
        } catch (IllegalArgumentException | NullPointerException e) {
            return;
        }

        mainTB.findViewById(R.id.toolbar_previous).setVisibility(View.VISIBLE);
        mainTB.findViewById(R.id.allgroups_text).setVisibility(View.GONE);
        mainTB.findViewById(R.id.toolbar_add).setVisibility(View.GONE);
        mainTB.findViewById(R.id.toolbar_add_linear_layout).setVisibility(View.GONE);
        mainTB.findViewById(R.id.toolbar_reps).setVisibility(View.GONE);
        mainTB.findViewById(R.id.toolbar_groups).setVisibility(View.GONE);
        mainTB.findViewById(R.id.groups_horizontal).setVisibility(View.GONE);
        mainTB.findViewById(R.id.hamburger_icon).setVisibility(View.GONE);
        mainTB.findViewById(R.id.takeaction).setVisibility(View.GONE);

        pageRoot.addView(gc);
    }


    /**
     * Subscribing to a topic is at this point as simple as subscribing to a topic via the name of
     * the avocacy group of interest.  In the future, these rules may become more complicated
     *
     * @param group
     */
    public void subscribeToGroup(Group group, final boolean refresh, final Callback<Boolean> callback) {

        subscriptionCompleted = false;

        try {
            FirebaseMessaging.getInstance()
                    .subscribeToTopic(group.getGroupKey().replaceAll("\\s+", ""));
        } catch (Exception e) {
            Log.e(TAG, "Error subscribing to firebase notifications");
            return;
        }

        /* Add the group to the remote database and refresh all relavent lists */
        SessionManager.INSTANCE.addGroupForCurrentUser(group, new Callback<Boolean>() {
            @Override
            public boolean onExecuted(Boolean data) {

                if (!subscriptionCompleted) {
                    if (refresh) {
                        GroupManager.INSTANCE.refreshGroupsAndActionList();
                        if (callback != null) {
                            callback.onExecuted(true);
                        }
                    }
                    subscriptionCompleted = true;
                } else {
                    return false;
                }
                return false;
            }
        });
    }

    public void unSubscribeFromGroup(Group group, final boolean refresh, final Callback<Boolean> callback) {

        try {
            FirebaseMessaging.getInstance()
                    .unsubscribeFromTopic(group.getGroupKey().replaceAll("\\s+", ""));
        } catch (Exception e) {
            Log.e(TAG, "Error subscribing to firebase notifications");
        }

        SessionManager.INSTANCE.removeGroupForCurrentUser(group, new Callback<Boolean>() {
            @Override
            public boolean onExecuted(Boolean data) {

                if (refresh) {
                    GroupManager.INSTANCE.refreshGroupsAndActionList();
                    callback.onExecuted(data);
                }
                return false;
            }
        });

    }

    public void onBackPress() {
        //if prior screen was group detail container
        if (gc != null && gc.getParent() != null) {
            pageRoot.removeView(gc);
            gc = null;
        }
        else {
            MODE = GroupType.USER;
            mainTB.findViewById(R.id.toolbar_previous).setVisibility(View.GONE);
            mainTB.findViewById(R.id.allgroups_text).setVisibility(View.GONE);
            mainTB.findViewById(R.id.takeaction).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.groups_horizontal).setVisibility(View.VISIBLE);
            mainTB.findViewById(R.id.hamburger_icon).setVisibility(View.VISIBLE);
        }
        toggleGroups(MODE);
    }

    /**
     * Find the group for a specific key
     *
     * @param actionKey
     * @return
     */
    public Group findGroupWithKey(String actionKey) {
        for (Group group : allGroupsData) {
            if (group.getGroupKey().equals(actionKey)) {
                return group;
            }
        }

        return null;
    }

    public GroupType getMODE() {
        return MODE;
    }

    public boolean isGroupPageVisible() {
        return groupPageVisible;
    }

    public void toggleActionDetailView(final VoicesMainActivity activity, final Context context, final Action action) {
        actionDetails = (RelativeLayout) groupPage.findViewById(R.id.actions_details_container);
        AvenirTextView groupTitle = (AvenirTextView) actionDetails.findViewById(R.id.actions_detail_group_title);
        groupTitle.setText(action.getGroupName());
        ImageView previousButton = (ImageView) actionDetails.findViewById(R.id.action_detail_previous_icon);
        ImageView actionImage = (ImageView) actionDetails.findViewById(R.id.actions_detail_group_icon);
        Picasso.with(context)
                .load(action.getImageUrl())
                .placeholder(R.drawable.spinner_moving)
                .error(R.drawable.voices_icon)
                .fit()
                .into(actionImage);
        AvenirBoldTextView actionTitle = (AvenirBoldTextView) actionDetails.findViewById(R.id.actions_detail_title);
        actionTitle.setText(action.getTitle());
        final Animation animShow = AnimationUtils.loadAnimation(context, R.anim.view_show);
        final Animation animHide = AnimationUtils.loadAnimation(context, R.anim.view_hide);
        isExpanded1 = false;
        isExpanded2 = false;
        final LinearLayout expandingView1 = (LinearLayout) actionDetails.findViewById(R.id.view_expanding_1);
        AvenirTextView expandingViewText1 = (AvenirTextView) expandingView1.findViewById(R.id.expanding_title);
        final ImageView expandingViewImage1 = (ImageView) expandingView1.findViewById(R.id.expanding_button);
        final AvenirTextView hiddenView1 = (AvenirTextView) expandingView1.findViewById(R.id.hidden_view);
        expandingViewText1.setText(VoicesApplication.getContext().getString(R.string.important));
        expandingView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded1) {
                    expandingViewImage1.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.minus_icon));
                    hiddenView1.setText(action.getBody());
                    hiddenView1.setVisibility(View.VISIBLE);
                    hiddenView1.startAnimation(animShow);
                    isExpanded1 = true;
                } else {
                    expandingViewImage1.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.toolbar_add));
                    hiddenView1.setText("");
                    hiddenView1.setVisibility(View.GONE);
                    hiddenView1.startAnimation(animHide);
                    isExpanded1 = false;
                }
            }
        });
        final LinearLayout expandingView2 = (LinearLayout) actionDetails.findViewById(R.id.view_expanding_2);
        AvenirTextView expandingViewText2 = (AvenirTextView) expandingView2.findViewById(R.id.expanding_title);
        final ImageView expandingViewImage2 = (ImageView) expandingView2.findViewById(R.id.expanding_button);
        final TextView hiddenView2 = (TextView) expandingView2.findViewById(R.id.hidden_view);
        expandingViewText2.setText(VoicesApplication.getContext().getString(R.string.say));
        expandingView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded2) {
                    expandingViewImage2.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.minus_icon));
                    int voicesOrange = VoicesApplication.getContext().getResources().getColor(R.color.voices_orange);
                    String response;
                    String script = action.getScript();
                    if ((action == null) || (script == null)) {
                        response = VoicesApplication.getContext().getString(R.string.response_4);
                        hiddenView2.setText(response);
                        Spannable span = new SpannableString(response);
                        span.setSpan(new ForegroundColorSpan(voicesOrange), 17, 28, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        span.setSpan(new ForegroundColorSpan(voicesOrange), 88, 138, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        hiddenView2.setText(span);
                    } else {
                        response = action.getScript();
                        hiddenView2.setText(response);
                    }
                    hiddenView2.setVisibility(View.VISIBLE);
                    hiddenView2.startAnimation(animShow);
                    isExpanded2 = true;
                } else {
                    expandingViewImage2.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.toolbar_add));
                    hiddenView2.setText("");
                    hiddenView2.setVisibility(View.GONE);
                    hiddenView2.startAnimation(animHide);
                    isExpanded2 = false;
                }
            }
        });
        final LinearLayout expandingView3 = (LinearLayout) actionDetails.findViewById(R.id.view_expanding_3);
        AvenirTextView expandingViewText3 = (AvenirTextView) expandingView3.findViewById(R.id.expanding_title);
        expandingViewText3.setText(VoicesApplication.getContext().getString(share));
        ImageView expandingViewImage3 = (ImageView) expandingView3.findViewById(R.id.expanding_button);
        expandingViewImage3.setImageDrawable(VoicesApplication.getContext().getResources().getDrawable(R.drawable.share_button));
        expandingView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, action.getTitle() + " " + action.getBody());
                ((VoicesMainActivity) expandingView3.getContext()).startActivity(Intent.createChooser(intent, "Share"));
            }
        });
        LinearLayout contactRepsEmptyState = (LinearLayout) actionDetails.findViewById(R.id.actions_detail_reps_error);
        Button addAddressButton = (Button) contactRepsEmptyState.findViewById(R.id.actions_detail_address_button);
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoicesMainActivity activity = (VoicesMainActivity) v.getContext();
                activity.saveAddressForDetail(action.getLevel(), action.getActionType());
            }
        });



        if (activity.locationSaved() && (action.getActionType() == null || !(action.getActionType().equals("singleRep")))) {
            //actionsDetailsErrorLayout.setVisibility(View.GONE);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(VoicesApplication.getContext());
            String savedLocation = prefs.getString("address", "");
            double lat = Double.parseDouble(prefs.getString("lat", "38.8976763"));
            double lon = Double.parseDouble(prefs.getString("lon", "-77.0387238"));
            refreshActionDetailReps(
                    savedLocation,
                    lat,
                    lon,
                    activity,
                    action.getLevel(),
                    action.getActionType(),
                    null);
        }

        if (action.getActionType() != null && action.getActionType().equals("singleRep")) {
            contactRepsEmptyState.setVisibility(View.GONE);
            Representative representative = action.getSingleRep();
            ListView actionsDetailListView = (ListView) actionDetails.findViewById(R.id.actions_detail_reps_list);
            actionsDetailListView.setVisibility(View.VISIBLE);
            ArrayList<Representative> representatives = new ArrayList<Representative>();
            representatives.add(0, representative);
            actionsDetailListView.setAdapter(new RepresentativesListAdapter(actionsDetailListView.getContext(),
                    R.layout.reps_item,
                    representatives));
        }

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleGroups(GroupType.ACTION);
                expandingViewImage1.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.toolbar_add));
                expandingViewImage2.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.toolbar_add));
                hiddenView1.setText("");
                hiddenView1.setVisibility(View.GONE);
                hiddenView2.setText("");
                hiddenView2.setVisibility(View.GONE);
            }
        });
    }

    public void refreshActionDetailReps(final String locationString,
                                        double repLat,
                                        double repLong,
                                        final VoicesMainActivity activity,
                                        final long repsType,
                                        final String actionType,
                                        final Representative singleRep) {
        RepresentativesManager.RepresentativesType repType = RepresentativesManager.RepresentativesType.CONGRESS;
        if (repsType == 2) {
            repType = RepresentativesManager.RepresentativesType.STATE_LEGISLATORS;
        }
        if (repsType == 3) {
            repType = RepresentativesManager.RepresentativesType.COUNCIL_MEMBERS;
        }
        LinearLayout contactRepsEmptyState = (LinearLayout) actionDetails.findViewById(R.id.actions_detail_reps_error);
        contactRepsEmptyState.setVisibility(View.GONE);
        RESTUtil.makeRepresentativesRequest(locationString, repLat, repLong, repType,
                new Callback2<ArrayList<Representative>, RepresentativesManager.RepresentativesType>() {
                    @Override
                    public boolean onExecuted(final ArrayList<Representative> data,
                                              final RepresentativesManager.RepresentativesType type) {
                        activity.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                            }
                        });

                        final ArrayList<Representative> result = data;

                        activity.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                ListView representativesListView = (ListView) actionDetails.findViewById(R.id.actions_detail_reps_list);
                                if (representativesListView != null) {
                                    representativesListView.setAdapter(
                                            new RepresentativesListAdapter(actionDetails.getContext(), R.layout.reps_item, data));
                                    representativesListView.setVisibility(View.VISIBLE);
                                }
                                if ((result != null) && (result.size() > 0)) {
                                } else {
                                }
                            }
                        });
                        return false;
                    }
                });
    }


    public void setDefferredGroupKey(final String defferredGroupKey, boolean subscribe) {

        this.deferredGroupKey = defferredGroupKey;
        this.deferredGroupKey = this.deferredGroupKey.toUpperCase().replace("HTTPS://TRYVOICES.COM/", "");


        if (!subscribe) {
            return;
        }

        final WeakHandler handler = new WeakHandler();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (GroupManager.INSTANCE.allGroupsData.size() == 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                    }
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Group> userGroups = groupPage.getUserGroups();
                        if (userGroups != null) {
                            for (Group group : userGroups) {
                                if (group.getGroupKey().equals(GroupManager.this.deferredGroupKey)) {
                                    return;
                                }
                            }
                        }
                        AnalyticsManager.INSTANCE.trackEvent("SUBSCRIBE_EVENT",
                                GroupManager.this.deferredGroupKey,
                                SessionManager.INSTANCE.getCurrentUserToken(), "none", null);
                    }
                });
            }
        });
        thread.start();
    }

    public void setAllGroupsData(ArrayList<Group> allGroupsData) {
        this.allGroupsData = allGroupsData;
    }
}
