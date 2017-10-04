package com.mobilonix.voices.groups.model;

public class ActionDetail {
    String actionDetailName;
    String actionDetailDescription;

    public ActionDetail(String actionDetailName, String actionDetailDescription) {
        this.actionDetailName = actionDetailName;
        this.actionDetailDescription = actionDetailDescription;
    }

    public String getActionDetailName() {
        return actionDetailName;
    }

    public String getActionDetailDescription() {
        return actionDetailDescription;
    }
}
