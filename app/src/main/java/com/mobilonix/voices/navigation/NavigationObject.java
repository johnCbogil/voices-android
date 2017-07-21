package com.mobilonix.voices.navigation;

public class NavigationObject {
    private String title;
    private String description;

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public NavigationObject(String title, String description){
        this.title = title;
        this.description = description;
    }

    public void setTitle(String newTitle){
        newTitle = title;
    }

    public void setDescription(String newDescription){
        description = newDescription;
    }
}
