package com.mobilonix.voices.representatives.model;

import com.mobilonix.voices.representatives.RepresentativesManager;

import java.util.ArrayList;

public class RepresentativesPage {

    ArrayList<Representative> representatives;
    RepresentativesManager.RepresentativesType type;

    public RepresentativesPage(ArrayList<Representative> representatives,
                               RepresentativesManager.RepresentativesType type) {
        this.representatives = representatives;
        this.type = type;
    }

    public ArrayList<Representative> getRepresentatives() {
        return representatives;
    }

    public RepresentativesManager.RepresentativesType getType() {
        return type;
    }
}
