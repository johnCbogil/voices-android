package com.mobilonix.voices.data.api;

import com.mobilonix.voices.data.api.util.HttpRequestor;
import com.mobilonix.voices.data.model.Politico;

import java.io.IOException;
import java.util.List;

/**
 * Created by cakiadeg on 6/28/16.
 */

//TODO this may be better as an abstract class due to all of the standard private methods
public interface ApiEngine {

    void initialize(double latitude, double longitude, HttpRequestor requestor);
    List<Politico> retrieveData() throws IOException;
}
