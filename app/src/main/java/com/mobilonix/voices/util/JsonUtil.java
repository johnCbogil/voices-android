package com.mobilonix.voices.util;

import com.mobilonix.voices.VoicesApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class JsonUtil {

    private static String convertStreamToString(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            //TODO handle exception
        }
        return outputStream.toString();
    }

    public static JSONObject getJsonFromResource(int jsonResource)  {
        InputStream inputStream = VoicesApplication.getContext().getResources().openRawResource(jsonResource); // getting XML
        if(jsonResource > 0){
            try {
                JSONObject jsonObject = new JSONObject(convertStreamToString(inputStream));
                return jsonObject;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
