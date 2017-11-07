package com.mobilonix.voices.groups.model;

import android.support.annotation.NonNull;

import com.mobilonix.voices.representatives.model.Representative;

public class Action implements Comparable<Action> {

    private String actionKey;
    private String body;
    private String groupKey;
    private String groupName;
    private String imageUrl;
    private long level;
    private String subject;
    private long timeStamp;
    private String title;
    private String script;
    private String actionType;
    private Representative singleRep;

    public Action(String actionKey,
                  String body,
                  String groupKey,
                  String groupName,
                  String imageUrl,
                  long level,
                  String subject,
                  long timeStamp,
                  String title,
                  String script,
                  String actionType,
                  Representative singleRep
                  ) {

        this.actionKey = actionKey;
        this.body = body;
        this.groupKey = groupKey;
        this.groupName = groupName;
        this.imageUrl = imageUrl;
        this.level = level;
        this.subject = subject;
        this.timeStamp = timeStamp;
        this.title = title;
        this.script = script;
        this.actionType = actionType;
        this.singleRep = singleRep;
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

    public long getLevel(){
        return level;
    }

    public String getSubject() {
        return subject;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getTitle() {
        return title;
    }

    public String getScript(){
        return script;
    }

    public String getActionType(){
        return actionType;
    }

    public Representative getSingleRep(){
        return singleRep;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int compareTo(@NonNull Action compareAction) {
        long originalTimestamp = this.getTimeStamp();
        long compareTimestamp= compareAction.getTimeStamp();
        return (int)(compareTimestamp - originalTimestamp);
    }
}
