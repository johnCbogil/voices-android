package com.mobilonix.voices.data.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by cakiadeg on 7/1/16.
 */
public class UrlConnectionRequestor implements HttpRequestor {

    @Override
    public String makeGetRequest(String url) {

        URL apiUrl;
        HttpURLConnection urlConnection;

        try {

            apiUrl = new URL(url);
            urlConnection = (HttpURLConnection) apiUrl.openConnection();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return "malformed url";
        } catch (IOException e) {
            e.printStackTrace();
            return "connection error";
        }

        try {

            StringBuilder sb = new StringBuilder();

            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                sb.append(inputLine).append("\n");
            }

            in.close();
            return sb.toString();

        } catch(IOException e) {

            e.printStackTrace();
            return "connection error";

        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * TODO Will implement soon
     *
     * @param URL
     * @return
     */
    @Deprecated
    @Override
    public String makePostRequest(String URL) {
        return null;
    }
}
