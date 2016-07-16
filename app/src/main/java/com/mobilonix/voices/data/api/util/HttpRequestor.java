package com.mobilonix.voices.data.api.util;

/**
 * Created by cakiadeg on 7/1/16.
 */
public interface HttpRequestor {

    String makeGetRequest(String URL);
    String makePostRequest(String URL);
}
