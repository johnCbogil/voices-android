package com.mobilonix.voices.data.api;

import android.app.DownloadManager;

import com.mobilonix.voices.data.model.Politico;
import com.mobilonix.voices.representatives.RepresentativesManager;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Request;

/**
 * Created by cakiadeg on 7/16/16.
 */
public interface ApiEngine {

    Request generateRequest(double latitude, double longitude);
    ArrayList<Politico> parseData(String response) throws IOException;
    RepresentativesManager.RepresentativesType getRepresentativeType();
}
