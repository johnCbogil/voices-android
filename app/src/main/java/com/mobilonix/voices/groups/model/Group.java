package com.mobilonix.voices.groups.model;

import java.util.ArrayList;

/**
 * Model representing an advocacy group
 */
public class Group {

    String groupName;
    String groupCategory;
    String groupDescription;
    String groupImageUrl;
    ArrayList<Policy> policies;

    public Group(String groupName, String groupCategory, String groupDescription, String groupImageUrl, ArrayList<Policy> policies) {
        this.groupName = groupName;
        this.groupCategory = groupCategory;
        this.groupDescription = groupDescription;
        this.policies = policies;
        this.groupImageUrl = groupImageUrl;
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
}
