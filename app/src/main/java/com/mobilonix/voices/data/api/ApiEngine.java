package com.mobilonix.voices.data.api;

import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;

public interface ApiEngine {

    Request generateRequest(double latitude, double longitude);
    ArrayList<Politico> parseData(String response) throws IOException;
    //TODO: Do we need this or not?
    RepresentativesManager.RepresentativesType getRepresentativeType();
}
