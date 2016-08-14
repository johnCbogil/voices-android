package com.mobilonix.voices.groups.model;

public class Action {


    String actionKey;
    String body;
    String groupKey;
    String groupName;
    String imageUrl;
    String subject;
    String timeStamp;
    String title;

    public Action(String actionKey,
                  String body,
                  String groupKey,
                  String groupName,
                  String imageUrl,
                  String subject,
                  String timeStamp,
                  String title) {

        this.actionKey = actionKey;
        this.body = body;
        this.groupKey = groupKey;
        this.groupName = groupName;
        this.imageUrl = imageUrl;
        this.subject = subject;
        this.timeStamp = timeStamp;
        this.title = title;

    }

    public String getActionKey() {
        return actionKey;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public String getBody() {
        return body;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getSubject() {
        return subject;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getTitle() {
        return title;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
