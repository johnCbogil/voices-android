package com.mobilonix.voices.groups;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesMainActivity;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.model.Policy;
import com.mobilonix.voices.groups.ui.GroupPage;
import com.mobilonix.voices.session.SessionManager;

import java.util.ArrayList;
import java.util.zip.Inflater;

public enum GroupManager {

    INSTANCE;

    GroupPage groupPage;

    GroupType MODE;

    public enum GroupType {
        ACTION,
        USER,
        ALL
    }

    boolean groupPageVisible = false;

    ArrayList<Group> USER_GROUPS_DUMMY_DATA = new ArrayList<>();
    ArrayList<Group> ACTION_GROUPS_DUMMY_DATA = new ArrayList<>();
    ArrayList<Group> ALL_GROUPS_DUMMY_DATA = new ArrayList<>();

    ArrayList<Group> userGroupsData = new ArrayList<>();
    ArrayList<Group> actionGroupsData = new ArrayList<>();
    ArrayList<Group> allGroupsData = new ArrayList<>();

    /* Instance initialization for all you noobs :) */
    {

        ArrayList<Integer> actions = new ArrayList<>();
        actions.add(1);
        actions.add(2);

        /* Sub for user specific groups that should be pulled remotely from user account */
        USER_GROUPS_DUMMY_DATA.add(
                new Group("Electronic Frontier Foundation",
                        "Digital Rights",
                        "",
                        "https://www.eff.org/files/2015/03/02/eff-og-3.png", "",
                        null,
                        actions,
                        "BUM"));

        /* Sub for actions that should be pulled remotely from user account */
        ACTION_GROUPS_DUMMY_DATA.add(
                new Group("Electronic Frontier Foundation",
                        "Privacy Rights",
                        "Tell the FBI Not to abuse its massive biometric database.",
                        "https://www.eff.org/files/2015/03/02/eff-og-3.png", "",
                        null,
                        actions,
                        "BUM"));
        ACTION_GROUPS_DUMMY_DATA.add(
                new Group("Electronic Frontier Foundation",
                        "Encryption",
                        "Join EFA allies in NYC on Thursday 6/30.",
                        "https://www.eff.org/files/2015/03/02/eff-og-3.png",
                        "",
                        null,
                        actions,
                        "BUM"));

        /* Sub for all groups that should be pulled remotely from user account */
        ALL_GROUPS_DUMMY_DATA.add(
                new Group("Electronic Frontier Foundation",
                        "Digital Rights",
                        "",
                        "https://www.eff.org/files/2015/03/02/eff-og-3.png", "", null, actions, "BUM"));
         /* Sub for all groups that should be pulled remotely from user account */
        ALL_GROUPS_DUMMY_DATA.add(
                new Group("League of Women Voters",
                        "Women's Healthcare",
                        "",
                        "http://www.lwvbn.org/images/LWV_OpenLogo.jpg", "", null, actions, "BUM"));

         /* Sub for all groups that should be pulled remotely from user account */
        ALL_GROUPS_DUMMY_DATA.add(
                new Group("Planned Parenthood",
                        "Civic Engagement",
                        "",
                        "https://c2.staticflickr.com/6/5295/5553094952_711984489f.jpg", "", null, actions, "BUM"));

         /* Sub for all groups that should be pulled remotely from user account */
        ALL_GROUPS_DUMMY_DATA.add(
                new Group("American Civil Liberties Union",
                        "Civil Liberties",
                        "",
                        "http://humanrightsconnected.org/s/assets/images/blank_200_200_smediaremotehttps_pbs.twimg.comprofile_images705877503504568320irplaegC_200_200.png_0_0_100___multiply_c1.png", "", null, actions, "BUM"));


        userGroupsData.addAll(USER_GROUPS_DUMMY_DATA);
        actionGroupsData.addAll(ACTION_GROUPS_DUMMY_DATA);
    }

    public void toggleGroupPage(ViewGroup pageRoot, boolean state) {

        //String groupName, String groupCategory, String groupDescription, String groupImageUrl, ArrayList< Policy > policies

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
            SessionManager.INSTANCE.fetchAllGroupsFromDatabase(new Callback<ArrayList<Group>>() {

                @Override
                public boolean onExecuted(ArrayList<Group> data) {
                    groupPage.setAllGroups(data);

                    return false;
                }
            }, new Callback<ArrayList<Group>>() {
                @Override
                public boolean onExecuted(ArrayList<Group> data) {

                    groupPage.setUserGroups(data);
                    return false;
                }
            });

            groupPage.setActionGroups(actionGroupsData);
            //groupPage.setUserGroups(userGroupsData);

            toggleGroups(GroupType.ACTION);

            groupPageVisible = true;
        } else {
            if(groupPage != null) {
                pageRoot.removeView(groupPage);
            }

            groupPageVisible  = false;
        }
    }

    /**
     * Add to all group list
     *
     * @param groups
     */
    public void setAllGroupList(ArrayList<Group> groups) {
        allGroupsData.clear();
        allGroupsData.addAll(groups);
    }

    public void toggleGroups(GroupType groupType) {

        Toolbar toolbar = ((VoicesMainActivity)groupPage.getContext()).getToolbar();

        if(groupType == GroupType.ACTION) {
            groupPage.findViewById(R.id.action_groups_list).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.user_groups_list).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_list).setVisibility(View.GONE);

            toolbar.findViewById(R.id.groups_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_add_groups).setVisibility(View.VISIBLE);

            MODE = GroupType.ACTION;

        } else if(groupType == GroupType.USER) {
            groupPage.findViewById(R.id.action_groups_list).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_list).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.all_groups_list).setVisibility(View.GONE);

            toolbar.findViewById(R.id.groups_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_selection_text).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_add_groups).setVisibility(View.VISIBLE);

            MODE = GroupType.USER;

        } else if(groupType == GroupType.ALL) {
            groupPage.findViewById(R.id.action_groups_list).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_list).setVisibility(View.GONE);
            groupPage.findViewById(R.id.all_groups_list).setVisibility(View.VISIBLE);

            toolbar.findViewById(R.id.primary_toolbar_back_arrow).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.action_add_groups).setVisibility(View.GONE);
            toolbar.findViewById(R.id.groups_selection_text).setVisibility(View.GONE);
            toolbar.findViewById(R.id.action_selection_text).setVisibility(View.GONE);

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
     * Subscribing to a topic is at this point as simple as subscribing to a topic via the name of
     * the avocacy group of interest.  In the future, these rules may become more complicated
     *
     * @param group
     */
    public void subscribeToGroup(Group group) {
        FirebaseMessaging.getInstance().subscribeToTopic(group.getGroupName());
    }

    public void onBackPress() {

        MODE = GroupType.USER;

        Toolbar toolbar = ((VoicesMainActivity)groupPage.getContext()).getToolbar();

        toolbar.findViewById(R.id.primary_toolbar_back_arrow).setVisibility(View.GONE);
        groupPage.findViewById(R.id.action_groups_list).setVisibility(View.GONE);
        groupPage.findViewById(R.id.user_groups_list).setVisibility(View.VISIBLE);
        groupPage.findViewById(R.id.all_groups_list).setVisibility(View.GONE);
        toolbar.findViewById(R.id.action_add_groups).setVisibility(View.VISIBLE);

        toolbar.findViewById(R.id.groups_selection_text).setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.action_selection_text).setVisibility(View.VISIBLE);

        toolbar.findViewById(R.id.action_selection_text).setBackgroundResource(R.drawable.button_back);
        toolbar.findViewById(R.id.groups_selection_text).setBackgroundResource(R.drawable.button_back_selected);
    }

    public GroupType getMODE() {
        return MODE;
    }

    public boolean isGroupPageVisible() {
        return groupPageVisible;
    }
}
