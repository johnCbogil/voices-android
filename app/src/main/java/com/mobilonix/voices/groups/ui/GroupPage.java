package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.util.AvenirBoldTextView;

import java.util.ArrayList;

public class GroupPage extends FrameLayout {

    ArrayList<Action> actions;
    ArrayList<Group> userGroups;
    ArrayList<Group> allGroups;

    boolean userGroupsSet = false;
    boolean actionsSet = false;

    EntityContainer actionsContainer;
    EntityContainer userGroupsContainer;

    public GroupPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setActions(ArrayList<Action> fetchedActions) {
        actions = new ArrayList<Action>();

        for(Action action : fetchedActions){
            if(action.getTimeStamp() <= System.currentTimeMillis()/1000){
                actions.add(action);
            }
        }

        actionsContainer = (EntityContainer)findViewById(R.id.actions_container);
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

        userGroupsContainer = (EntityContainer)findViewById(R.id.user_groups_container);
        userGroupsContainer.addItems(userGroups,
                GroupManager.GroupType.USER);

        userGroupsSet = true;
    }

    public void setAllGroups(ArrayList<Group> allGroups) {
        this.allGroups = allGroups;
        EntityContainer allGroupsContainer = (EntityContainer)findViewById(R.id.all_groups_container);
        allGroupsContainer.addItems(allGroups,
                GroupManager.GroupType.USER);
        AvenirBoldTextView actionsButton=(AvenirBoldTextView)allGroupsContainer.findViewById(R.id.actions_button);
        actionsButton.setVisibility(View.GONE);
        AvenirBoldTextView groupsButton=(AvenirBoldTextView)allGroupsContainer.findViewById(R.id.groups_button);
        groupsButton.setVisibility(View.GONE);
        LinearLayout actionsGroupsLinearLayout=(LinearLayout)allGroupsContainer.findViewById(R.id.actions_groups_linear_layout);
        actionsGroupsLinearLayout.setVisibility(View.GONE);
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
                        action.setImageUrl(group.getGroupImageUrl());
                        userActions.add(action);
                    }
                }
            }
        }
        return userActions;
    }

    public boolean hasUserGroupWithKey(String name) {
        for(Group group : userGroups) {
            if(group.getGroupKey().equals(name)) {
                return true;
            }
        }

        return false;
    }

    //TODO: Do we need these or not?
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
