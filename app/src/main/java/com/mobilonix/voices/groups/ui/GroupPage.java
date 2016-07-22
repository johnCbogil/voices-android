package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Group;

import java.util.ArrayList;

public class GroupPage extends FrameLayout {

    ArrayList<Group> actionGroups;
    ArrayList<Group> userGroups;

    boolean userGroupsSet = false;
    boolean actionGroupsSet = false;

    public GroupPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setActionGroups(ArrayList<Group> actionGroups) {
        this.actionGroups = actionGroups;

        if(!actionGroupsSet) {
            ListView actionGroupsList = ((ListView) findViewById(R.id.action_groups_list));
            actionGroupsList
                    .setAdapter(new GroupListAdapter(getContext(),
                            R.layout.cell_group,
                            actionGroups,
                            GroupManager.GroupType.ACTION));

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

    public void clearAllGroups() {
        ListView actionGroupsList = ((ListView) findViewById(R.id.action_groups_list));
        ListView userGroupsList = ((ListView) findViewById(R.id.user_groups_list));

        actionGroupsList.setAdapter(new ArrayAdapter<Group>(getContext(), R.layout.cell_group, new ArrayList<Group>()));
        userGroupsList.setAdapter(new ArrayAdapter<Group>(getContext(), R.layout.cell_group, new ArrayList<Group>()));

        userGroupsSet = false;
        actionGroupsSet = false;
    }



}
