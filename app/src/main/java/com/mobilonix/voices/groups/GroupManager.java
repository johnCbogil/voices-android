package com.mobilonix.voices.groups;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobilonix.voices.R;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.model.Policy;
import com.mobilonix.voices.groups.ui.GroupPage;

import java.util.ArrayList;
import java.util.zip.Inflater;

public enum GroupManager {

    INSTANCE;

    GroupPage groupPage;

    public enum GroupType {
        ACTION,
        USER
    }

    boolean groupPageVisible = false;

    ArrayList<Group> USER_GROUPS_DUMMY_DATA = new ArrayList<>();
    ArrayList<Group> ACTION_GROUPS_DUMMY_DATA = new ArrayList<>();

    /* Instance initialization for all you noobs :) */
    {
        USER_GROUPS_DUMMY_DATA.add(
                new Group("Electronic Frontier Foundation",
                        "Digital Rights",
                        "",
                        "https://www.eff.org/files/2015/03/02/eff-og-3.png", null));
        ACTION_GROUPS_DUMMY_DATA.add(
                new Group("Electronic Frontier Foundation",
                        "Privacy Rights",
                        "Tell the FBI Not to abuse its massive biometric database.",
                        "https://www.eff.org/files/2015/03/02/eff-og-3.png", null));
        ACTION_GROUPS_DUMMY_DATA.add(
                new Group("Electronic Frontier Foundation",
                        "Encryption",
                        "Join EFA allies in NYC on Thursday 6/30.",
                        "https://www.eff.org/files/2015/03/02/eff-og-3.png", null));
    }

    public void toggleGroupPage(ViewGroup pageRoot, boolean state) {

        //String groupName, String groupCategory, String groupDescription, String groupImageUrl, ArrayList< Policy > policies



        if(state) {
            if(groupPage == null) {
                LayoutInflater inflater = (LayoutInflater)pageRoot.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                groupPage = (GroupPage)inflater.inflate(R.layout.view_groups_screen, null, false);
            }

            /* Add the groups view to the main page*/
            pageRoot.removeView(groupPage);
            pageRoot.addView(groupPage);

            /* TODO: Make a request here via asynchronous callback to load the actual group data*/
            /* TODO: We wanto retrieve this from cache first, otherwise if not present, re-request it from backend */
            groupPage.setUserGroups(USER_GROUPS_DUMMY_DATA);
            groupPage.setActionGroups(ACTION_GROUPS_DUMMY_DATA);

            toggleActionGroups(true);

            groupPageVisible = true;
        } else {
            if(groupPage != null) {
                pageRoot.removeView(groupPage);
            }

            groupPageVisible  = false;
        }
    }

    public void toggleActionGroups(boolean state) {
        if(state) {
            groupPage.findViewById(R.id.action_groups_list).setVisibility(View.VISIBLE);
            groupPage.findViewById(R.id.user_groups_list).setVisibility(View.GONE);
        } else {
            groupPage.findViewById(R.id.action_groups_list).setVisibility(View.GONE);
            groupPage.findViewById(R.id.user_groups_list).setVisibility(View.VISIBLE);
        }
    }

    public boolean isGroupPageVisible() {
        return groupPageVisible;
    }
}
