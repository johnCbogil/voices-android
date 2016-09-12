package com.mobilonix.voices.groups;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.model.Policy;
import com.mobilonix.voices.groups.ui.EntityContainer;
import com.mobilonix.voices.groups.ui.GroupPage;
import com.mobilonix.voices.groups.ui.PolicyListAdapter;
import com.mobilonix.voices.representatives.RepresentativesManager;
import com.mobilonix.voices.session.SessionManager;
import com.mobilonix.voices.util.ViewUtil;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public enum GroupManager {

    INSTANCE;

    boolean isRefreshing = false;

    private final String TAG = GroupManager.class.getCanonicalName();

    GroupPage groupPage;

    GroupType MODE;

    public enum GroupType {
        ACTION,
        USER,
        ALL
    }

    boolean groupPageVisible = false;

    ArrayList<Group> allGroupsData = new ArrayList<>();

    public void toggleGroupPage(ViewGroup pageRoot, boolean state) {

        if(isRefreshing) {
            ((VoicesMainActivity)pageRoot.getContext()).toggleProgressSpinner(true);
        } else {
            ((VoicesMainActivity)pageRoot.getContext()).toggleProgressSpinner(false);
        }

        if(state) {
            if(groupPage == null) {
                LayoutInflater inflater = (LayoutInflater)pageRoot.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                groupPage = (GroupPage)inflater.inflate(R.layout.view_groups_screen, null, false);
            }


            /* Add the groups view to the main page*/
            if(groupPage.getParent()!=null)
                ((ViewGroup)groupPage.getParent()).removeView(groupPage);

            pageRoot.addView(groupPage);

            /* TODO: Make a request here via asynchronous callback to load the actual group data*/
            /* TODO: We wanto retrieve this from cache first, otherwise if not present, re-request it from backend */
            refreshGroupsAndActionList();

            toggleGroups(GroupType.ACTION);


            groupPageVisible = true;
        } else {
            if(groupPage != null) {

                ((VoicesMainActivity)pageRoot.getContext()).toggleProgressSpinner(false);
                pageRoot.removeView(groupPage);
            }

            groupPageVisible  = false;
        }
    }

    public void refreshGroupsAndActionList() {
        /* TODO: Make a request here via asynchronous callback to load the actual group data*/
        /* TODO: We wanto retrieve this from cache first, otherwise if not present, re-request it from backend */

        isRefreshing = true;
        ((VoicesMainActivity)groupPage.getContext())
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
                        ((VoicesMainActivity)groupPage.getContext())
                                .toggleProgressSpinner(isRefreshing);

                        return false;
                    }
                });

                return false;
            }
        });
    }

    public void toggleGroups(GroupType groupType) {

        Toolbar toolbar = ((VoicesMainActivity)groupPage.getContext()).getToolbar();

        if(groupType == GroupType.ACTION) {

            groupPage.findViewById(R.id.actions_container).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.GONE);

            ((EntityContainer)groupPage.findViewById(R.id.actions_container)).setType(groupType);

            toolbar.findViewById(R.id.groups_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.groups_selection_text).setBackgroundResource(R.drawable.button_back);
            toolbar.findViewById(R.id.action_selection_text).setBackgroundResource(R.drawable.button_back_selected);
            toolbar.findViewById(R.id.action_add_groups).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.all_groups_info_text).setVisibility(View.GONE);


            MODE = GroupType.ACTION;

        } else if(groupType == GroupType.USER) {

            groupPage.findViewById(R.id.actions_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.GONE);

            ((EntityContainer)groupPage.findViewById(R.id.user_groups_container)).setType(groupType);

            toolbar.findViewById(R.id.groups_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_add_groups).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.all_groups_info_text).setVisibility(View.GONE);

            MODE = GroupType.USER;

        } else if(groupType == GroupType.ALL) {

            groupPage.findViewById(R.id.actions_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_container).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_container).setVisibility(View.VISIBLE);

            ((EntityContainer)groupPage.findViewById(R.id.all_groups_container)).setType(groupType);

            toolbar.findViewById(R.id.primary_toolbar_back_arrow).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.all_groups_info_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_add_groups).setVisibility(View.GONE);
            toolbar.findViewById(R.id.groups_selection_text).setVisibility(View.GONE);
            toolbar.findViewById(R.id.action_selection_text).setVisibility(View.GONE);
            toolbar.findViewById(R.id.groups_selection_text).setBackgroundResource(R.drawable.button_back_selected);
            toolbar.findViewById(R.id.action_selection_text).setBackgroundResource(R.drawable.button_back);

            toolbar.findViewById(R.id.primary_toolbar_back_arrow).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPress();
                }
            });

            MODE = GroupType.ALL;
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

        ImageView groupsImage = (ImageView)dialog.findViewById(R.id.group_info_image);

        Picasso.with(context)
                .load(group.getGroupImageUrl())
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .placeholder(R.drawable.voices_icon)
                .error(R.drawable.voices_icon)
                .fit()
                .into(groupsImage);

        TextView groupsInfoDescription = (TextView) dialog.findViewById(R.id.group_info_description);
        TextView groupsInfoPolicyText = (TextView)dialog
                .findViewById(R.id.group_info_policy_text);
        final Button groupsFollowGroupsButton =
                (Button)dialog.findViewById(R.id.follow_groups_button);
        ListView policyList = (ListView)dialog.findViewById(R.id.groups_policy_list);

        groupsInfoDescription.setText(group.getGroupDescription());
        groupsInfoPolicyText.setText(group.getGroupCategory());

        final ArrayList<Group> userGroups = groupPage.getUserGroups();
        if(userGroups != null) {
            for (Group g : groupPage.getUserGroups()) {
                if (g.getGroupKey().equals(group.getGroupKey())) {
                    groupsFollowGroupsButton.setText(R.string.unfollow_groups_text);
                }
            }
        }

        groupsFollowGroupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* check if we're already subscribed to the group */
                if(userGroups != null) {
                    for (Group g : groupPage.getUserGroups()) {
                        if (g.getGroupKey().equals(group.getGroupKey())) {

                            unSubscribeFromGroup(group, true, new Callback<Boolean>() {
                                @Override
                                public boolean onExecuted(Boolean data) {
                                    groupsFollowGroupsButton.setText(R.string.follow_groups_text);
                                    return false;
                                }
                            });

                            return;
                        }
                    }
                }

                subscribeToGroup(group, true, new Callback<Boolean>() {
                    @Override
                    public boolean onExecuted(Boolean data) {
                        groupsFollowGroupsButton.setText(R.string.unfollow_groups_text);
                        return false;
                    }
                });
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
        toolbar.findViewById(R.id.primary_toolbar_back_arrow).setVisibility(View.GONE);
        final TextView actionSelectionButton = (TextView)toolbar.findViewById(R.id.action_selection_text);
        final TextView groupsSelectionButton = (TextView)toolbar.findViewById(R.id.groups_selection_text);

        actionSelectionButton.setBackgroundResource(R.drawable.button_back);
        actionSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.voices_orange));
        groupsSelectionButton.setBackgroundResource(R.drawable.button_back_selected);
        groupsSelectionButton.setTextColor(ViewUtil.getResourceColor(R.color.white));
    }
    /**
     * Find the group for a specific key
     *
     * @param actionKey
     * @return
     */
    public Group findGroupWithKey(String actionKey) {
        for (Group group : allGroupsData) {
            if(group.getGroupKey().equals(actionKey)) {
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

    public void toggleActionDialog(Context context, Action action) {
        final Dialog actionDialog = new Dialog(context);
        actionDialog.setTitle("Take Action");
        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.setContentView(R.layout.dialog_actions);

        TextView actionTitle  = (TextView)actionDialog.findViewById(R.id.actions_title);
        TextView actionSubject  = (TextView)actionDialog.findViewById(R.id.actions_subject);
        TextView actionDescription = (TextView)actionDialog.findViewById(R.id.actions_description);
        ImageView actionImage = (ImageView)actionDialog.findViewById(R.id.actions_image);

        Button contactRepresentativesButton = (Button)actionDialog.findViewById(R.id.actions_button_contact_representatives);

        actionTitle.setText(action.getGroupName());
        actionSubject.setText(action.getSubject());
        actionDescription.setText(action.getBody());

        Picasso.with(context)
                .load(action.getImageUrl())
                .placeholder(R.drawable.placeholder_spinner)
                .error(R.drawable.representatives_place_holder_male)
                .fit()
                .into(actionImage);

        contactRepresentativesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionDialog.dismiss();
                RepresentativesManager.INSTANCE.selectRepresentativesTab();
            }
        });


        actionDialog.show();
    }

    public void togglePolicyDialog(Context context, Policy policy, Action action, final Dialog parentDialog) {

        final Dialog actionDialog;

        actionDialog = new Dialog(context);
        actionDialog.setTitle("Take Action");
        actionDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        actionDialog.setContentView(R.layout.dialog_policies);

        TextView policiesTitle  = (TextView)actionDialog.findViewById(R.id.policies_title);
        TextView policiesDescription = (TextView)actionDialog.findViewById(R.id.policies_description);
        Button contactRepresentativesButton = (Button)actionDialog.findViewById(R.id.button_contact_representatives);

        if(policy != null) {
            policiesTitle.setText(policy.getPolicyName());
            policiesDescription.setText(policy.getPolicyDescription());
        }

        contactRepresentativesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(parentDialog != null) {
                    parentDialog.dismiss();
                }

                actionDialog.dismiss();
                RepresentativesManager.INSTANCE.selectRepresentativesTab();
            }
        });

        actionDialog.show();
    }
}
