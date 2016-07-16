package com.mobilonix.voices.data.api.util;

//package com.viacom.voicesapis;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class AsyncGetLatLonFromAddy extends AsyncTask<String, String, String> {
//
//        Context context;
//
//        AsyncGetLatLonFromAddy(Context context) {
//
//            this.context = context;
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            try{
//                URL url = new URL(  params[0] );
//                String address = params[1];
//
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                try {
//
//                    StringBuilder sb = new StringBuilder();
//                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//                    String inputLine;
//
//                    while ((inputLine = in.readLine()) != null) {
//                        Log.d("    inputLine", inputLine);
//                        sb.append(inputLine).append("\n");
//                    }
//
//                    in.close();
//                    return sb.toString();
//                } finally {
//                    urlConnection.disconnect();
//                }
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
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
//            Log.d("Goog url", "JSON goog api Data" + result);
//
//                try{
//                    JSONObject jObj = new JSONObject(result);
//
//                    for(int i = 0; i< jObj.length(); i++){
//                        if(i == 0){
//                            JSONArray jArray = jObj.getJSONArray("results");
//                            JSONObject geometryObject = jArray.getJSONObject(i).getJSONObject("geometry");
//                            JSONObject locationObject = geometryObject.getJSONObject("location");
//
//                            String lat = locationObject.getString("lat");
//                            String lon = locationObject.getString("lng");
//                            GeoUtil geoUtil = new GeoUtil(context, Double.parseDouble(google_latitude), Double.parseDouble(google_longitude));
//                        }
//                    }
//
//                    congress_url = congress_beginning + congress_pre_latitude + google_latitude + congress_pre_longitude + google_longitude + congress_api_key;
//                    Log.d("goog async cgurl", congress_url);
//
//                    state_url = state_beginning + state_pre_latitude + google_latitude + state_pre_longitude + google_longitude + state_api_key;
//                    Log.d("goog async state", state_url);
//
//                    if (mPageNumber == 0){
//                        new ConMyAsyncTask(getActivity().getApplicationContext()).execute(congress_url);
//                    } else if (mPageNumber == 1){
//
//                        new StateMyAsyncTask(getActivity().getApplicationContext()).execute(state_url);
//                    } else {
//                        Log.d("FragmentDisplay", "MyAsyncTask_Congress cant get location");
//                        //TODO get address and borough from query text
//
//                        new AsyncGetCouncilInfo().execute(geoUtil.getAddressLine(), geoUtil.getBorough());
//                    }
//                } catch (JSONException e){
//                        e.printStackTrace();
//                }
//        }
//    }
