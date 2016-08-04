package com.mobilonix.voices.groups.ui;

import android.content.Context;
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
    boolean actionGroupsSet = false;

    public GroupPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;

        if(!actionGroupsSet) {
            ListView actionGroupsList = ((ListView) findViewById(R.id.action_groups_list));
            actionGroupsList
                    .setAdapter(new ActionListAdapter(getContext(),
                            R.layout.cell_group,
                            actions));

            actionGroupsSet = true;
        }
    }

    public void setUserGroups(ArrayList<Group> userGroups) {
        this.userGroups = userGroups;
        if(!userGroupsSet) {
            ListView userGroupsList = ((ListView) findViewById(R.id.user_groups_list));
            userGroupsList
                    .setAdapter(new GroupListAdapter(getContext(),
                            R.layout.cell_group,
                            userGroups,
                            GroupManager.GroupType.USER));

            userGroupsSet = true;

        }
    }

    public void setAllGroups(ArrayList<Group> allGroups) {

        GeneralUtil.toast("All groups set: " + allGroups);

        ListView allGroupsList = ((ListView) findViewById(R.id.all_groups_list));
        allGroupsList
                    .setAdapter(new GroupListAdapter(getContext(),
                            R.layout.cell_group,
                            allGroups,
                            GroupManager.GroupType.ALL));

    }

    /**
     * Force a clearing of all groups and actions
     */
    public void clearAllGroups() {

        ListView actionGroupsList = ((ListView) findViewById(R.id.action_groups_list));
        ListView userGroupsList = ((ListView) findViewById(R.id.user_groups_list));

        actionGroupsList.setAdapter(new ArrayAdapter<Group>(getContext(), R.layout.cell_group, new ArrayList<Group>()));
        userGroupsList.setAdapter(new ArrayAdapter<Group>(getContext(), R.layout.cell_group, new ArrayList<Group>()));

        userGroupsSet = false;
        actionGroupsSet = false;
    }

}
