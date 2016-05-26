package com.johnbogil.voices.district;

import android.content.Context;
import android.util.Log;

import com.johnbogil.voices.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cakiadeg on 4/23/16.
 */
public class CouncilParser {
    
    private JSONObject mCouncilMembers;

    public CouncilParser(Context context, int jsonResource) {

        mCouncilMembers = getJsonFromResource(context, jsonResource);
    }

    public Politician politicianFromDistrict(Integer district) {

        Politician politico = new Politician();

        try {
            JSONObject member = mCouncilMembers.getJSONObject(district.toString());

            String firstName = member.getString("firstName");
            String lastName = member.getString("lastName");
            String phoneNumbers = member.getString("phoneNumber");
            String photos = member.getString("photoURLPath");
            String twitter = member.getString("twitter");
            String email = member.getString("email");

            politico.setFullName(firstName + " " + lastName);
            politico.setEmailAddy(email);
            politico.setPhoneNumber(phoneNumbers);
            politico.setPicUrl(photos);
            politico.setTwitterHandle(twitter);

        } catch (JSONException e) {

            //TODO handle exception
        }
        return politico;
    }

    private String convertStreamToString(InputStream inputStream) {

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

    private JSONObject getJsonFromResource(Context context, int jsonResource)  {

        InputStream inputStream = context.getResources().openRawResource(jsonResource); // getting XML

        if(jsonResource > 0){

            try {
                JSONObject jsonObject = new JSONObject(convertStreamToString(inputStream));

                Log.d("TAG", jsonObject.toString());
                return jsonObject;

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
