package com.mobilonix.voices.groups.model;

public class Action implements Comparable<Action> {

    String actionKey;
    String body;
    String groupKey;
    String groupName;
    String imageUrl;
    long level;
    String subject;
    long timeStamp;
    String title;
    String script;

    public Action(String actionKey,
                  String body,
                  String groupKey,
                  String groupName,
                  String imageUrl,
                  long level,
                  String subject,
                  long timeStamp,
                  String title,
                  String script
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
        //this.actionDetails = actionDetails;

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
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public int compareTo(Action compareAction) {
        long originalTimestamp = this.getTimeStamp();
        long compareTimestamp= compareAction.getTimeStamp();
        return (int)(compareTimestamp - originalTimestamp);
    }
}
