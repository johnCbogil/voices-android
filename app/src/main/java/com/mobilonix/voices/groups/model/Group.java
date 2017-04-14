package com.mobilonix.voices.groups.model;

import java.util.ArrayList;

public class Group {

    String groupName;
    String groupCategory;
    String groupDescription;
    String groupImageUrl;
    String extendedDescription;
    String groupWebsite;
    String groupKey;
    boolean debug = false;

    ArrayList<Policy> policies;
    ArrayList<String> actions;

    public Group(String groupName,
                 String groupCategory,
                 String groupDescription,
                 String groupImageUrl,
                 String extendedDescription,
                 String groupWebsite,
                 ArrayList<Policy> policies,
                 ArrayList<String> actions,
                 String groupKey) {
        this.groupName = groupName;
        this.groupCategory = groupCategory;
        this.groupDescription = groupDescription;
        this.groupImageUrl = groupImageUrl;
        this.extendedDescription = extendedDescription;
        this.groupWebsite = groupWebsite;
        this.policies = policies;
        this.actions = actions;
        this.groupKey = groupKey;
    }

    public ArrayList<Policy> getPolicies() {
        return policies;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public String getGroupWebsite(){
        return groupWebsite;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    //Return the indices of associated actions obtained from the list of all actions
    public ArrayList<String> getActions() {
        return actions;
    }
}
