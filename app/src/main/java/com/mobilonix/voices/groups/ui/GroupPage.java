package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mobilonix.voices.R;
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

        EntityContainer actionsContainer = (EntityContainer)findViewById(R.id.actions_container);
        actionsContainer.addItems(selectUserActions(actions, userGroups),
                GroupManager.GroupType.ACTION);

        actionsSet = true;
    }

    /**
     * Set the user specific groups for a page
     *
     * @param userGroups
     */
    public void setUserGroups(ArrayList<Group> userGroups) {
        this.userGroups = userGroups;

        EntityContainer userGroupsContainer = (EntityContainer)findViewById(R.id.user_groups_container);
        userGroupsContainer.addItems(userGroups,
                GroupManager.GroupType.USER);

        userGroupsSet = true;
    }

    public void setAllGroups(ArrayList<Group> allGroups) {
        this.allGroups = allGroups;

        EntityContainer userGroupsContainer = (EntityContainer)findViewById(R.id.all_groups_container);
        userGroupsContainer.addItems(allGroups,
                GroupManager.GroupType.USER);

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
