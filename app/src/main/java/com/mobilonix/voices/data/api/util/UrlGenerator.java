package com.mobilonix.voices.data.api.util;

import android.os.Bundle;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlGenerator {

    private static final char AMPERSAND = '&';
    private static final char QUESTION_MARK = '?';
    private static final char EQUALS = '=';

    String mBaseUrl;
    Bundle mNameValuePairs;

    public UrlGenerator(String baseURL, Bundle nameValuePairs) {
        mBaseUrl = baseURL;
        mNameValuePairs = nameValuePairs;
    }

    public URL generateGetUrl() {

        String fullUrlAsString = "";

        if(!mBaseUrl.isEmpty() && mNameValuePairs != null) {

            fullUrlAsString = mBaseUrl + QUESTION_MARK;

            for (String name : mNameValuePairs.keySet()) {
                fullUrlAsString += name + EQUALS + mNameValuePairs.getString(name) + AMPERSAND;
            }
        }

        try {
            return new URL(fullUrlAsString);
        } catch(MalformedURLException e) {
            return null;
        }
    }

    //TODO: Do we need this or not?
    public String generateGetUrlString() {

        String fullUrlAsString = "";

        if(!mBaseUrl.isEmpty() && mNameValuePairs != null) {

            fullUrlAsString = mBaseUrl + QUESTION_MARK;

            for (String name : mNameValuePairs.keySet()) {
                fullUrlAsString += name + EQUALS + mNameValuePairs.getString(name) + AMPERSAND;
            }
        }
        //Delete last AMPERSAND
        //fullUrlAsString = fullUrlAsString.substring(0,fullUrlAsString.length()-1);
        return fullUrlAsString;
    }
}

