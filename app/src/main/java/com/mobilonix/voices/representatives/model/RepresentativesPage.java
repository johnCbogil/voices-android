package com.mobilonix.voices.representatives.model;

import java.util.ArrayList;

public class RepresentativesPage {

    ArrayList<Representative> representatives;

    public RepresentativesPage(ArrayList<Representative> representatives) {
        this.representatives = representatives;
    }

    public ArrayList<Representative> getRepresentatives() {
        return representatives;
    }

}
