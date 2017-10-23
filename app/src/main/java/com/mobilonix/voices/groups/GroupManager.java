package com.mobilonix.voices.groups;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.mobilonix.voices.groups.model.Policy;
import com.mobilonix.voices.groups.ui.EntityContainer;
import com.mobilonix.voices.groups.ui.GroupPage;
import com.mobilonix.voices.groups.ui.PolicyListAdapter;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.representatives.ui.RepresentativesListAdapter;
import com.mobilonix.voices.representatives.ui.RoundedTransformation;
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.util.AvenirBoldTextView;
import com.mobilonix.voices.util.AvenirTextView;
import com.mobilonix.voices.util.RESTUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.mobilonix.voices.R.string.share;

public enum GroupManager {

    INSTANCE;

    boolean isRefreshing = false;

    private final String TAG = GroupManager.class.getCanonicalName();

    GroupPage groupPage;

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

    ArrayList<Group> userGroups = new ArrayList<Group>();

    ArrayList<Group> allGroups = new ArrayList<Group>();

    String defferredGroupKey = null;

    Dialog responseDialog;

    boolean isExpanded1;
    boolean isExpanded2;

    public void toggleGroupPage(ViewGroup pageRoot, boolean state) {
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
            /* TODO: We wanto retrieve this from cache first, otherwise if not present, re-request it from backend */
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
                        isRefreshing = false;
                        ((VoicesMainActivity) groupPage.getContext())
                                .toggleProgressSpinner(isRefreshing);

                        return false;
                    }
                });

                if (defferredGroupKey != null) {
                    subscribeToGroup(findGroupWithKey(defferredGroupKey), true, null);
                    defferredGroupKey = null;
                }

                return false;
            }
        });
    }

    public void toggleGroups(GroupType groupType) {

        Toolbar toolbar = ((VoicesMainActivity) groupPage.getContext()).getToolbar();

        int indicatorBlue = VoicesApplication.getContext()
                .getResources().getColor(R.color.indicator_blue);
        int indicatorGrey = VoicesApplication.getContext()
                .getResources().getColor(R.color.indicator_grey);

        if (groupType == GroupType.ACTION) {
            groupPage.findViewById(R.id.actions_container).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.actions_details_container).setVisibility(View.GONE);

            toolbar.setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_reps).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_groups).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.groups_horizontal).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.reps_horizontal).setVisibility(View.INVISIBLE);
            toolbar.findViewById(R.id.takeaction).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_previous).setVisibility(View.GONE);
            toolbar.findViewById(R.id.allgroups_text).setVisibility(View.GONE);

            ((EntityContainer) groupPage.findViewById(R.id.actions_container)).setType(groupType);
            toolbar.findViewById(R.id.toolbar_add).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_add_linear_layout).setVisibility(View.VISIBLE);

            ((AvenirBoldTextView)(groupPage.findViewById(R.id.actions_container))
                    .findViewById(R.id.actions_button)).setTextColor(indicatorBlue);
            ((AvenirBoldTextView)(groupPage.findViewById(R.id.actions_container))
                    .findViewById(R.id.groups_button)).setTextColor(indicatorGrey);
            MODE = GroupType.ACTION;

        } else if (groupType == GroupType.USER) {

            groupPage.findViewById(R.id.actions_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.actions_details_container).setVisibility(View.GONE);

            toolbar.setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_reps).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_groups).setVisibility(View.VISIBLE);

            ((EntityContainer) groupPage.findViewById(R.id.user_groups_container)).setType(groupType);

            toolbar.findViewById(R.id.toolbar_add).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_add_linear_layout).setVisibility(View.VISIBLE);

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

            toolbar.findViewById(R.id.toolbar_previous).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.allgroups_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.toolbar_add).setVisibility(View.GONE);
            toolbar.findViewById(R.id.toolbar_add_linear_layout).setVisibility(View.GONE);
            toolbar.findViewById(R.id.toolbar_reps).setVisibility(View.GONE);
            toolbar.findViewById(R.id.toolbar_groups).setVisibility(View.GONE);
            toolbar.findViewById(R.id.groups_horizontal).setVisibility(View.GONE);
            toolbar.findViewById(R.id.hamburger_icon).setVisibility(View.GONE);
            toolbar.findViewById(R.id.toolbar_previous).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPress();
                }
            });

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
            toolbar.setVisibility(View.GONE);
            MODE = GroupType.ACTION_DETAIL;
        }
    }

    /**
     * This is a quick way to test if group subscriptions are working
     *
     * @param context
     * @param group
     */
    public void toggleSubscribeToGroupDialog(Context context, final Group group) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_groups);
        dialog.setTitle(group.getGroupName());

        ImageView groupsImage = (ImageView) dialog.findViewById(R.id.group_info_image);

        Picasso.with(context)
                .load(group.getGroupImageUrl())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.voices_icon)
                .error(R.drawable.voices_icon)
                .transform(new RoundedTransformation(10, 0))
                .fit()
                .into(groupsImage);

        TextView groupsInfoDescription = (TextView) dialog.findViewById(R.id.group_info_description);
        TextView groupsInfoPolicyText = (TextView) dialog
                .findViewById(R.id.group_info_policy_text);
        TextView groupsWebsite = (TextView) dialog
                .findViewById(R.id.group_website);
        final Button groupsFollowGroupsButton =
                (Button) dialog.findViewById(R.id.follow_groups_button);
        ListView policyList = (ListView) dialog.findViewById(R.id.groups_policy_list);

        groupsInfoDescription.setText(group.getGroupDescription());
        groupsInfoPolicyText.setText(group.getGroupCategory());
        groupsWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                //group.getGroupWebsite())
                dialog.getContext().startActivity(intent);
            }
        });
        groupsWebsite.setText(group.getGroupWebsite());

        final ArrayList<Group> userGroups = groupPage.getUserGroups();
        if (userGroups != null) {
            for (Group g : groupPage.getUserGroups()) {
                if (g.getGroupKey().equals(group.getGroupKey())) {
                    groupsFollowGroupsButton.setText(R.string.following_groups_text);
                }
            }
        }

        final ProgressDialog pd = new ProgressDialog(dialog.getContext());
        pd.setTitle("Following....");
        pd.setMessage("");
        pd.setIndeterminate(true);
        pd.setCancelable(false);

        groupsFollowGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!groupsFollowGroupsButton.getText().toString().equals(v.getContext()
                        .getString(R.string.following_groups_text))) {
                    pd.show();
                }
                /* check if we're already subscribed to the group */
                if (userGroups != null) {
                    for (Group g : groupPage.getUserGroups()) {
                        if (g.getGroupKey().equals(group.getGroupKey())) {
                            final Dialog followDialog;
                            followDialog = new Dialog(groupPage.getContext());
                            followDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            followDialog.setContentView(R.layout.dialog_follow);
                            Button unfollowButton = (Button) followDialog.findViewById(R.id.unfollow_button);
                            Button cancelButton = (Button) followDialog.findViewById(R.id.cancel_button);
                            unfollowButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pd.setTitle("Unfollowing....");
                                    pd.show();
                                    unSubscribeFromGroup(group, true, new Callback<Boolean>() {
                                        @Override
                                        public boolean onExecuted(Boolean data) {
                                            pd.dismiss();
                                            groupsFollowGroupsButton.setText(R.string.follow_groups_text);
                                            if (data) {
                                                AnalyticsManager.INSTANCE.trackEvent("UNSUBSCRIBE_EVENT",
                                                        group.getGroupKey(),
                                                        SessionManager.INSTANCE.getCurrentUserToken(), "none", null);
                                            }
                                            followDialog.dismiss();
                                            return false;
                                        }
                                    });
                                    return;
                                }
                            });
                            cancelButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pd.dismiss();
                                    followDialog.dismiss();
                                }
                            });
                            followDialog.show();
                        }
                    }
                }

                if(!groupPage.hasUserGroupWithKey(group.getGroupKey())) {

                    subscribeToGroup(group, true, new Callback<Boolean>() {
                        @Override
                        public boolean onExecuted(Boolean data) {
                            groupsFollowGroupsButton.setText(R.string.following_groups_text);

                            pd.dismiss();

                            if (data) {
                                AnalyticsManager.INSTANCE.trackEvent("SUBSCRIBE_EVENT",
                                        group.getGroupKey(),
                                        SessionManager.INSTANCE.getCurrentUserToken(), "none", null);
                            }

                            return false;
                        }
                    });
                }
            }
        });

        policyList.setAdapter(new PolicyListAdapter(context,
                R.layout.policy_list_item,
                group.getPolicies(),
                dialog));

        dialog.show();

    }

    boolean subscriptionCompleted = false;

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
        MODE = GroupType.USER;
        toggleGroups(GroupType.USER);
        Toolbar toolbar = ((VoicesMainActivity) groupPage.getContext()).getToolbar();
        toolbar.findViewById(R.id.toolbar_previous).setVisibility(View.GONE);
        toolbar.findViewById(R.id.allgroups_text).setVisibility(View.GONE);
        toolbar.findViewById(R.id.takeaction).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.groups_horizontal).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.hamburger_icon).setVisibility(View.VISIBLE);
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
        AvenirTextView groupTitle = (AvenirTextView)actionDetails.findViewById(R.id.actions_detail_group_title);
        groupTitle.setText(action.getGroupName());
        ImageView previousButton = (ImageView)actionDetails.findViewById(R.id.action_detail_previous_icon);
        ImageView actionImage = (ImageView)actionDetails.findViewById(R.id.actions_detail_group_icon);
        Picasso.with(context)
                .load(action.getImageUrl())
                .placeholder(R.drawable.spinner_moving)
                .error(R.drawable.reps_male)
                .fit()
                .into(actionImage);
        AvenirBoldTextView actionTitle = (AvenirBoldTextView)actionDetails.findViewById(R.id.actions_detail_title);
        actionTitle.setText(action.getTitle());
        final Animation animShow = AnimationUtils.loadAnimation(context, R.anim.view_show);
        final Animation animHide = AnimationUtils.loadAnimation(context, R.anim.view_hide);
        isExpanded1 = false;
        isExpanded2 = false;
        final LinearLayout expandingView1 = (LinearLayout)actionDetails.findViewById(R.id.view_expanding_1);
        AvenirTextView expandingViewText1 = (AvenirTextView)expandingView1.findViewById(R.id.expanding_title);
        final ImageView expandingViewImage1 = (ImageView)expandingView1.findViewById(R.id.expanding_button);
        final AvenirTextView hiddenView1 = (AvenirTextView)expandingView1.findViewById(R.id.hidden_view);
        expandingViewText1.setText(VoicesApplication.getContext().getString(R.string.important));
        expandingView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded1 == false) {
                    expandingViewImage1.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.minus_icon));
                    hiddenView1.setText(action.getBody());
                    hiddenView1.setVisibility(View.VISIBLE);
                    hiddenView1.startAnimation(animShow);
                    isExpanded1=true;
                } else {
                    expandingViewImage1.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.toolbar_add));
                    hiddenView1.setText("");
                    hiddenView1.setVisibility(View.GONE);
                    hiddenView1.startAnimation(animHide);
                    isExpanded1=false;
                }
            }
        });
        final LinearLayout expandingView2 = (LinearLayout)actionDetails.findViewById(R.id.view_expanding_2);
        AvenirTextView expandingViewText2 = (AvenirTextView)expandingView2.findViewById(R.id.expanding_title);
        final ImageView expandingViewImage2 = (ImageView)expandingView2.findViewById(R.id.expanding_button);
        final TextView hiddenView2 = (TextView)expandingView2.findViewById(R.id.hidden_view);
        expandingViewText2.setText(VoicesApplication.getContext().getString(R.string.say));
        expandingView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isExpanded2 == false) {
                    expandingViewImage2.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.minus_icon));
                    int voicesOrange = VoicesApplication.getContext().getResources().getColor(R.color.voices_orange);
                    String response;
                    String script = action.getScript();
                    if((action == null) || (script == null)) {
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
                    isExpanded2=true;
                } else {
                    expandingViewImage2.setImageDrawable(VoicesApplication.getContext().getDrawable(R.drawable.toolbar_add));
                    hiddenView2.setText("");
                    hiddenView2.setVisibility(View.GONE);
                    hiddenView2.startAnimation(animHide);
                    isExpanded2=false;
                }
            }
        });
        final LinearLayout expandingView3 = (LinearLayout)actionDetails.findViewById(R.id.view_expanding_3);
        AvenirTextView expandingViewText3 = (AvenirTextView)expandingView3.findViewById(R.id.expanding_title);
        expandingViewText3.setText(VoicesApplication.getContext().getString(share));
        ImageView expandingViewImage3 = (ImageView)expandingView3.findViewById(R.id.expanding_button);
        expandingViewImage3.setImageDrawable(VoicesApplication.getContext().getResources().getDrawable(R.drawable.share_button));
        expandingView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, action.getTitle() + " " + action.getBody());
                activity.startActivity(Intent.createChooser(intent, "Share"));
            }
        });
        LinearLayout contactRepsEmptyState = (LinearLayout)actionDetails.findViewById(R.id.actions_detail_reps_error);
        Button addAddressButton = (Button)contactRepsEmptyState.findViewById(R.id.actions_detail_address_button);
        addAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VoicesMainActivity activity =(VoicesMainActivity)v.getContext();
                activity.saveAddressForDetail(action.getLevel(), action.getActionType());
            }
        });
        if(activity.locationSaved()){
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
        if(repsType==2){
             repType = RepresentativesManager.RepresentativesType.STATE_LEGISLATORS;
        }
        if(repsType==3){
            repType = RepresentativesManager.RepresentativesType.COUNCIL_MEMBERS;
        }
        LinearLayout contactRepsEmptyState = (LinearLayout)actionDetails.findViewById(R.id.actions_detail_reps_error);
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

                        final ArrayList<Representative> result;

                        if(actionType!=null&&actionType.equals("singleRep")){
                            Representative representative = singleRep;
                            result = new ArrayList<>();
                            result.add(0, representative);
                        } else if ((data == null) || data.isEmpty()) {
                            result = new ArrayList<>();
                        } else {
                            result = data;
                        }

                        activity.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                ListView representativesListView = (ListView)actionDetails.findViewById(R.id.actions_detail_reps_list);
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

    public void togglePolicyDialog(Context context, Policy policy, final Action action, final Dialog parentDialog) {

        final Dialog actionDialog;

        actionDialog = new Dialog(context);
        actionDialog.setTitle("Take Action");
        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.setContentView(R.layout.dialog_policies);

        TextView policiesTitle = (TextView) actionDialog.findViewById(R.id.policies_title);
        TextView policiesDescription = (TextView) actionDialog.findViewById(R.id.policies_description);
        Button contactRepresentativesButton = (Button) actionDialog.findViewById(R.id.button_contact_representatives);

        if (policy != null) {
            policiesTitle.setText(policy.getPolicyName());
            policiesDescription.setText(policy.getPolicyDescription());
        }

        contactRepresentativesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (parentDialog != null) {
                    parentDialog.dismiss();
                }

                actionDialog.dismiss();
                RepresentativesManager.INSTANCE.selectRepresentativesTab();

            }
        });
        actionDialog.show();
    }


    public void setDefferredGroupKey(final String defferredGroupKey, boolean subscribe) {

        this.defferredGroupKey = defferredGroupKey;
        this.defferredGroupKey = this.defferredGroupKey.toUpperCase().replace("HTTPS://TRYVOICES.COM/", "");

        if(!subscribe) {
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
                        if(userGroups != null) {
                            for (Group group : userGroups) {
                                if (group.getGroupKey().equals(GroupManager.this.defferredGroupKey)) {
                                    return;
                                }
                            }
                        }
                        AnalyticsManager.INSTANCE.trackEvent("SUBSCRIBE_EVENT",
                                GroupManager.this.defferredGroupKey,
                                SessionManager.INSTANCE.getCurrentUserToken(), "none", null);
                    }
                });
            }
        });
        thread.start();
    }
}
