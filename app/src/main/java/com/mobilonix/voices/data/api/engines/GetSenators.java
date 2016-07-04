//package com.mobilonix.voices.data.api.engines;//package com.tryvoices.apis.api.engines;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.view.View;
//
//import com.mobilonix.voices.data.api.util.UrlGenerator;
//
//import org.json.JSONException;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.URL;
//
///**
// * Created by cakiadeg on 6/19/16.
// */
//public class GetSenators {
//
//    //  state strings
//    public String state_beginning = "http://openstates.org/api/v1//legislators/geo/?";
//    public String state_pre_latitude ="lat=";
//    public String state_pre_longitude = "long=";
//    public String apiKey = "apikey";
//    public String apiValue = "";
//    public String state_url;
//    boolean stateSwipe;
//
//    UrlGenerator url = new UrlGenerator("http://openstates.org/api/v1//legislators/geo/?");
//
//        @Override
//        protected String doInBackground(String... params) {
//            try{
//                Log.d("FragmentDisplay", "stateAsyncTask doInBackground");
//                url = new URL(state_url);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                try {
//
//                    StringBuilder sb = new StringBuilder();
//                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    String inputLine;
//                    while ((inputLine = in.readLine()) != null) {
//                        Log.d("    inputLine", inputLine);
//                        sb.append(inputLine).append("\n");
//                    }
//                    in.close();
//                    Log.d("     sb.toString()", sb.toString());
//                    return sb.toString();
//
//                } finally {
//                    urlConnection.disconnect();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute (String result){
//
//                if (result != null){
//
//                }else{
//
//                }
//
//
//            }
//        }
//}