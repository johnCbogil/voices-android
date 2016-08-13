package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;

import java.util.ArrayList;

public class GroupPage extends FrameLayout {

    ArrayList<Action> actions;
    ArrayList<Group> userGroups;
    ArrayList<Group> allGroups;

    boolean userGroupsSet = false;
    boolean actionsSet = false;

    public GroupPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setActions(ArrayList<Action> actions) {

        this.actions = actions;

        RecyclerView actionsRecycler = ((RecyclerView) findViewById(R.id.action_groups_list));
        actionsRecycler.setLayoutManager(new LinearLayoutManager(actionsRecycler.getContext()));
        actionsRecycler.setAdapter(new
                ActionListRecylerAdapter(actionsRecycler.getContext(),
                selectUserActions(actions, userGroups)));

        actionsSet = true;
    }

    /**
     * Set the user specific groups for a page
     *
     * @param userGroups
     */
    public void setUserGroups(ArrayList<Group> userGroups) {
        this.userGroups = userGroups;

        RecyclerView userGroupsRecycler = ((RecyclerView) findViewById(R.id.user_groups_list));
        userGroupsRecycler.setLayoutManager(new LinearLayoutManager(userGroupsRecycler.getContext()));
        userGroupsRecycler.setAdapter(new GroupListRecylerAdapter(getContext(),
                userGroups,
                GroupManager.GroupType.USER));

        userGroupsSet = true;
    }

    public void setAllGroups(ArrayList<Group> allGroups) {
        this.allGroups = allGroups;

        RecyclerView allGroupsRecycler = ((RecyclerView) findViewById(R.id.all_groups_list));
        allGroupsRecycler.setLayoutManager(new LinearLayoutManager(allGroupsRecycler.getContext()));
        allGroupsRecycler.setAdapter(new GroupListRecylerAdapter(getContext(),
                allGroups,
                GroupManager.GroupType.ALL));

    }

    /**
     * Get the user's specific actions, as long as they are subscribed to particular groups
     *
     * TODO: Optimize this based on data structure
     *
     * @param allActions
     * @param userGroups
     * @return
     */
    public ArrayList<Action> selectUserActions(ArrayList<Action> allActions, ArrayList<Group> userGroups) {

        ArrayList<Action> userActions = new ArrayList<>();

        for(Group group : userGroups) {
            ArrayList<String> groupActions = group.getActions();
            for (String actionString : groupActions) {
                for(Action action : allActions) {
                    if(action.getActionKey().equals(actionString)) {
                        userActions.add(action);
                    }
                }
            }
        }

        return userActions;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }

    public ArrayList<Group> getAllGroups() {
        return allGroups;
    }

    public ArrayList<Group> getUserGroups() {
        return userGroups;
    }
}
