package com.mobilonix.voices.data.api;

import com.mobilonix.voices.data.model.Politico;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cakiadeg on 7/16/16.
 */
public interface ApiUtil {

    String generateUrl(double latitude, double longitude);
    ArrayList<Politico> parseData(String response) throws IOException;
}
