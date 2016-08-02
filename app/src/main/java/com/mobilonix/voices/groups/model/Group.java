package com.mobilonix.voices.groups.model;

import java.util.ArrayList;

/**
 * Model representing an advocacy group
 */
public class Group {

    String groupName;
    String groupCategory;
    String groupDescription;
    String extendedDescription;
    String groupImageUrl;
    String groupKey;

    ArrayList<Policy> policies;
    ArrayList<Integer> actions;

    public Group(String groupName,
                 String groupCategory,
                 String groupDescription,
                 String groupImageUrl,
                 String extendedDescription,
                 ArrayList<Policy> policies,
                 ArrayList<Integer> actions, String groupKey) {
        this.groupName = groupName;
        this.groupCategory = groupCategory;
        this.groupDescription = groupDescription;
        this.policies = policies;
        this.extendedDescription = extendedDescription;
        this.groupImageUrl = groupImageUrl;
        this.actions = actions;
        this.groupKey = groupKey;
    }

    public ArrayList<Policy> getPolicies() {
        return policies;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public String getGroupImageUrl() {
        return groupImageUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getExtendedDescription() {
        return extendedDescription;
    }

    public String getGroupKey() {
        return groupKey;
    }

    /**
     * Return the indices of associated actions obtained from the list of all actions
     *
     * @return
     */
    public ArrayList<Integer> getActions() {
        return actions;
    }
}
