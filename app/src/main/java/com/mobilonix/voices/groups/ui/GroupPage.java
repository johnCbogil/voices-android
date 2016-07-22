package com.mobilonix.voices.groups.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.mobilonix.voices.groups.model.Group;

import java.util.ArrayList;

public class GroupPage extends FrameLayout {

    ArrayList<Group> actionGroups;
    ArrayList<Group> userGroups;

    public GroupPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setActionGroups(ArrayList<Group> actionGroups) {
        this.actionGroups = actionGroups;
    }

    public void setUserGroups(ArrayList<Group> userGroups) {
        this.userGroups = userGroups;
    }

}
