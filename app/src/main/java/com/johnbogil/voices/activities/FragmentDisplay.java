package com.johnbogil.voices.activities;


import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.johnbogil.voices.R;
import com.johnbogil.voices.congress.CongressClass;
import com.johnbogil.voices.congress.CongressListAdapter;
import com.johnbogil.voices.district.CouncilMemberAdapter;
import com.johnbogil.voices.district.CouncilMembersClass;
import com.johnbogil.voices.district.CouncilParser;
import com.johnbogil.voices.district.GeoUtil;
import com.johnbogil.voices.district.Politician;
import com.johnbogil.voices.gps.Tracker;
import com.johnbogil.voices.misc.ParseJSONData;
import com.johnbogil.voices.state.StateLegislatorAdapter;
import com.johnbogil.voices.state.StateLegislatorsClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FragmentDisplay extends Fragment {

    public static final String ARG_PAGE = "page";
    private int mPageNumber;
    private ParseJSONData parse_jSON_data;
    public ListView mListView;
    public SearchView mSearchView;
    public TextView fragment_list_activity_bottom_text_view;
    public SwipeRefreshLayout swipeRefreshLayout;
    Tracker gpsTracker;
    DataHandler dataHandler;


    CongressListAdapter adapter_congress;
    StateLegislatorAdapter adapter_state;
    CouncilMemberAdapter adapter_council;



    GeoUtil geoUtil;

    // google api client strings
    URL url;
    public String google_beginning = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    public String google_area_string;
    public String google_api_key = "&key=AIzaSyAe24dZyoXHT3PRew2PiD06-8B8yQT67-Q";
    public String google_url = null;
    public String google_latitude;
    public String google_longitude;
    public String latitude_ ="";
    public String longitude_ = "";

    //congress strings
    private CongressClass[] mCongressClasses;
    public String congress_beginning= "https://congress.api.sunlightfoundation.com/legislators/locate?";
    public String congress_pre_latitude ="latitude=";
    public String congress_pre_longitude = "&longitude=";
    public String congress_api_key = "&apikey=939e3373a07c468bac51ddd604ebba1f";
    public String congress_url;
    public String congress_image_url_beginning = "https://theunitedstates.io/images/congress/225x275/";
    public String congress_image_url_end = ".jpg";
    boolean conSwipe;

//  state strings
    private StateLegislatorsClass[] mStateLegislatorsClasses;
    public String state_beginning = "http://openstates.org/api/v1//legislators/geo/?";
    public String state_pre_latitude ="lat=";
    public String state_pre_longitude = "&long=";
    public String state_api_key = "&apikey=e39ba83d7c5b4e348db144c4b4c33108";
    public String state_url;
    boolean stateSwipe;

    //council strings
    private CouncilMembersClass[] mCouncilMembersClasses;

    //    Build fragment and put the page numbers in to Agruments
    public static FragmentDisplay create(int pageNumber) {
        FragmentDisplay fragmentDisplay = new FragmentDisplay();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragmentDisplay.setArguments(args);

        return fragmentDisplay;
    }

    /**
     * GC
     *
     * Pulls Data from database if available, then sets listview adapter appropriately per view
     * Otherwise it pulls data from server by  calling {@link FragmentDisplay#swipeViewLoad()} using
     * {@link ConMyAsyncTask} etc.

     * Sets Query hint if connection unavailable
     *
     * Sets refreshLayoutListener
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


/*        mCouncilMembersClasses = new CouncilMembersClass[1];
        mCouncilMembersClasses[0] = new CouncilMembersClass();

        mCouncilMembersClasses[0].setCouncilMembersClass(politico);*/

        mPageNumber = getArguments().getInt(ARG_PAGE);

        View rootView = inflater.inflate(R.layout.fragment_fragment_display, container, false);

        rootView.setId(mPageNumber);

        mListView = (ListView) rootView.findViewById(R.id.listView);
        mSearchView = (SearchView) rootView.findViewById(R.id.searchView);
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        fragment_list_activity_bottom_text_view = (TextView)
                getActivity().findViewById(R.id.fragment_list_activity_bottom_text_view);

        dataHandler = new DataHandler(getContext());
        dataHandler.open();

        if (dataHandler.returnConData().getCount() > 0 && dataHandler.returnStateData().getCount() > 0
                && dataHandler.returnCouncilData().getCount() > 0 ) {
            try {
                if(mPageNumber == 0) {
                    mSearchView.setQueryHint("Congress by location");
                    ParseJSONData parse_stored_congress = new ParseJSONData();
                    parse_stored_congress.setCongressClasses(getCongressFromSql());
                    Parcelable[] parcelables_congress = parse_stored_congress.getCongressClasses();
                    mCongressClasses = Arrays.copyOf(parcelables_congress, parcelables_congress.length, CongressClass[].class);
                    adapter_congress = new CongressListAdapter(getContext(), mCongressClasses);
                    mListView.setAdapter(adapter_congress);
                } else if (mPageNumber == 1) {

                    mSearchView.setQueryHint("State Legislators by location");
                    Log.d("FragmentDisplay", "onCreateView State Page" );
                    ParseJSONData parse_stored_state = new ParseJSONData();
                    parse_stored_state.setStateLegislatorsClasses(getStateFromSql());
                    Parcelable[] parcelables_state = parse_stored_state.getStateLegislatorsClasses();
                    mStateLegislatorsClasses = Arrays.copyOf(parcelables_state, parcelables_state.length, StateLegislatorsClass[].class);
                    adapter_state = new StateLegislatorAdapter(getContext(), mStateLegislatorsClasses);
                    mListView.setAdapter(adapter_state);

                } else if(mPageNumber == 2) {

                    Log.d("FragmentDisplay", "onCreateView() mPageNumber == 2");
                    mSearchView.setQueryHint("Council Member by location");
                    ParseJSONData parse_stored_council = new ParseJSONData();
                    parse_stored_council.setCouncilMembersClass(getCouncilFromSql());
                    Parcelable[] parcelables_council = parse_stored_council.getCouncilMembersClasses();
                    mCouncilMembersClasses = Arrays.copyOf(parcelables_council, parcelables_council.length, CouncilMembersClass[].class);
                    adapter_council = new CouncilMemberAdapter(getContext(), mCouncilMembersClasses);
                    mListView.setAdapter(adapter_council);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            dataHandler.close();

        } else {
            Log.d("FragmentDisplay", "onCreateView Tracker" );

            gpsTracker = new Tracker(getActivity().getApplicationContext());

            if (gpsTracker.canGetLocation()) {
                Log.d("FragmentDisplay", "onCreateView Tracker CanGetLocation" );

                if(geoUtil == null) {
                    geoUtil = new GeoUtil(getContext(), gpsTracker.getLatitude(), gpsTracker.getLongitude());
                }

                latitude_ = Double.toString(gpsTracker.getLatitude());
                longitude_ = Double.toString(gpsTracker.getLongitude());
                congress_url = congress_beginning + congress_pre_latitude + latitude_ + congress_pre_longitude + longitude_ + congress_api_key;
                state_url = state_beginning + state_pre_latitude + latitude_ + state_pre_longitude + longitude_ + state_api_key;

                Log.d("URL congress_url", congress_url);
                Log.d("URL state_url", state_url);
                swipeViewLoad();

            } else {

                mListView.setVisibility(View.GONE);
                Log.d("$$$ fragdis 201", Double.toString(gpsTracker.getLongitude()));
                Log.d("$$$ fragdis 202", Double.toString(gpsTracker.getLatitude()));
                Log.d("FragmentDisplay", "onCreateView No location" );

//              gpsTracker.showSettingsAlert();
            }
        }

        //----------------------------- setOnRefreshListener ---------------------------------------

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {

                Log.d("FragmentDisplay", "onRefreshListener" );

                gpsTracker = new Tracker(getActivity().getApplicationContext());
                if(geoUtil == null) {
                    geoUtil = new GeoUtil(getContext(), gpsTracker.getLatitude(), gpsTracker.getLongitude());
                }

                if (gpsTracker.canGetLocation()) {

                    Log.d("FragmentDisplay", "onRefreshListener canGetLocation" );

                    latitude_ = Double.toString(gpsTracker.getLatitude());
                    longitude_ = Double.toString(gpsTracker.getLongitude());

                    congress_url = congress_beginning + congress_pre_latitude + latitude_ + congress_pre_longitude + longitude_ + congress_api_key;
                    state_url = state_beginning + state_pre_latitude + latitude_ + state_pre_longitude + longitude_ + state_api_key;

                    //TODO Test this
                    conSwipe = true;
                    stateSwipe = true;

                    swipeRefreshLayout.setRefreshing(true);

                    new ConMyAsyncTask(getActivity().getApplicationContext()).execute(congress_url);
                    new StateMyAsyncTask(getActivity().getApplicationContext()).execute(state_url);
                    new AsyncGetCouncilInfo().execute(geoUtil.getAddressLine(), geoUtil.getBorough());

                } else {

                    Log.d("FragmentDisplay", "onRefreshListener no location");
                    listViewState();
                    swipeRefreshLayout.setRefreshing(false);
                    //TODO bug user about not being able to get location
                }
            }
        });

        return rootView;
    }

    /**
     * Executes refresh behavior dependent on mPageNumber.
     *
     * Executes {@link ConMyAsyncTask} ... etc dependent on page.
     */


    public void swipeViewLoad() {
        if (mPageNumber == 0){
//            congress
            mSearchView.setQueryHint("Congress by location");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("async1", "in!");
                    new ConMyAsyncTask(getActivity().getApplicationContext()).execute(congress_url);
                }
            });
        } else if (mPageNumber == 1) {
//            state
            mSearchView.setQueryHint("State Legislators by location");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("async2", "in!");
                    new StateMyAsyncTask(getActivity().getApplicationContext()).execute(state_url);
                    Log.d("FragmentDisplay", "swipeViewLoad new StateMyAsyncTask");

                }
            });

        } else if (mPageNumber == 2) {
            //Council
            mSearchView.setQueryHint("Council Member by location");
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //TODO check if geoutil works
                    Log.d("FragmentDisplay","swipeViewLoad Council");

                    new AsyncGetCouncilInfo().execute(geoUtil.getAddressLine(), geoUtil.getBorough());
                    //new AsyncGetCouncilInfo().execute("256 3rd Ave", 1);
                }
            });
        }
    }

    public void stopPullRefresh() {
        swipeRefreshLayout.setRefreshing(false); //called in on post execute of con/state async tasks
    }

    /**
     *
     *
     * 
     */
    SearchView.OnQueryTextListener mOnQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            //                input from searchView

            google_area_string = mSearchView.getQuery().toString().replaceAll(" ", "");
//                this is the search URL
            google_url = google_beginning + google_area_string + google_api_key;
//                this is the search asyncTask
            new AsyncGetLatLonFromAddy().execute(google_url);

            Log.d("SearchView", mSearchView.getQuery().toString());

            mSearchView.clearFocus(); // close the keyboard on load

            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    /**
     *
     * Executes when CouncilMember data is retrieved
     *
     * @param district
     */

    //TODO rename this method
    public boolean dataReady(int district) {

        Log.d("FragmentDisplay", "dataReady council");

        if(district != -1) {

            Politician politico;

            CouncilParser parser = new CouncilParser(getContext(),R.raw.nyc_district_data);
            politico = parser.politicianFromDistrict(district);
            politico.setDistrict("Council District " + district);

            parse_jSON_data = parseCouncilDetails(politico);  //error 4
            listViewCouncil();
            Log.i("TAG+1", politico + "");
            mCouncilMembersClasses[0] = new CouncilMembersClass();
            mCouncilMembersClasses[0].setCouncilMembersClass(politico);

            getCouncilPeople(politico);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                synchronized(mListView) {
                    if(mPageNumber == 2 ) listViewCouncil();
                }
                }
            });

            return true;

        } else {
            return false;
        }
    }

    /**
     *Uses Google API to convert Address to Lat / Lon
     *
     * Renamed from original app
     *
     */
    public class AsyncGetLatLonFromAddy extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("FragmentDisplay", "AsyncGetLatLonFromAddy");

            try{
                url = new URL (google_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {

                    StringBuilder sb = new StringBuilder();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        Log.d("    inputLine", inputLine);
                        sb.append(inputLine).append("\n");
                    }

                    in.close();
                    return sb.toString();
                } finally {
                    urlConnection.disconnect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingText();
        }

        @Override
        protected void onPostExecute (String result){
            Log.d("Goog url", "JSON goog api Data" + result);

                try{
                    JSONObject jObj = new JSONObject(result);

                    for(int i = 0; i< jObj.length(); i++){
                        if(i == 0){
                            JSONArray jArray = jObj.getJSONArray("results");
                            JSONObject geometryObject = jArray.getJSONObject(i).getJSONObject("geometry");
                            JSONObject locationObject = geometryObject.getJSONObject("location");

                            google_latitude = locationObject.getString("lat");
                            google_longitude = locationObject.getString("lng");
                            geoUtil = new GeoUtil(getContext(), Double.parseDouble(google_latitude), Double.parseDouble(google_longitude));
                        }
                        Log.d(" latitude/longitude_", "latitude: " + google_latitude + "longitude: " + google_longitude);
                    }

                    congress_url = congress_beginning + congress_pre_latitude + google_latitude + congress_pre_longitude + google_longitude + congress_api_key;
                    Log.d("goog async cgurl", congress_url);

                    state_url = state_beginning + state_pre_latitude + google_latitude + state_pre_longitude + google_longitude + state_api_key;
                    Log.d("goog async state", state_url);

                    if (mPageNumber == 0){
                        new ConMyAsyncTask(getActivity().getApplicationContext()).execute(congress_url);
                    } else if (mPageNumber == 1){

                        new StateMyAsyncTask(getActivity().getApplicationContext()).execute(state_url);
                    } else {
                        Log.d("FragmentDisplay", "MyAsyncTask_Congress cant get location");
                        //TODO get address and borough from query text

                        new AsyncGetCouncilInfo().execute(geoUtil.getAddressLine(), geoUtil.getBorough());
                    }
                } catch (JSONException e){
                        e.printStackTrace();
                }
        }
    }

    /**
     * Used to obtain Congressmen information from internet resource using lat-lon
     */

    public class ConMyAsyncTask extends AsyncTask<String, String, String> {

        Context context;

        ConMyAsyncTask(Context ctxt) {
            context = ctxt;
            Log.i("ConAsync", "Constructor");
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                Log.i("ConAsync", "doInBackground");
                url = new URL (congress_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {

                    StringBuilder sb = new StringBuilder();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        Log.d("    inputLine", inputLine);
                        sb.append(inputLine).append("\n");
                    }
                    in.close();
                    Log.d("     sb.toString()", sb.toString());
                    return sb.toString();

                } finally {
                    urlConnection.disconnect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingText();
        }

        @Override
        protected void onPostExecute (final String result){
            getActivity().runOnUiThread(new Runnable() {  //error 5
                @Override
                public void run() {
                    try {
                        if (result != null) {
                            parse_jSON_data = parseCongressDetails(result);  //error 4
                            getCongressPeople(result);
                            if(mPageNumber == 0) listViewCongress();
                            mListView.setVisibility(View.VISIBLE);
                            voiceText();
                        } else {
                            fragment_list_activity_bottom_text_view.setText(R.string.no_wifi_connection);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            stopPullRefresh();

            if(conSwipe == true){
                //TODO figure out why are they submitting "" here
                mSearchView.setQuery("",true);
                conSwipe = false;
            }
        }
    }

    /**
     * Used to convert database result to CongressClass objects and store in CongressClasses object
     *
     *
     * @param result Congressmen information retrieved from database query
     * @return
     * @throws JSONException
     */

    private CongressClass[] getCongressPeople(String result) throws JSONException {
        Log.i("FragmentDisplay","getCongressPeople | result: " + result);
        JSONObject results = new JSONObject(result);
        JSONArray congress_array = results.getJSONArray("results");
        CongressClass[] congressClasses = new CongressClass[congress_array.length()];
        dataHandler.open(); // error 2
        dataHandler.db.execSQL("DELETE FROM conTable");
        for (int i = 0; i < congress_array.length(); i++) {

            JSONObject congressObject = congress_array.getJSONObject(i);
            CongressClass congressClass = new CongressClass();
            congressClass.setFirst_name(congressObject.getString("first_name"));
            congressClass.setLast_name(congressObject.getString("last_name"));
            congressClass.setTitle(congressObject.getString("title"));
            congressClass.setOc_email(congressObject.getString("oc_email"));
            congressClass.setPhone(congressObject.getString("phone"));
            congressClass.setTerm_end(congressObject.getString("term_end"));
            congressClass.setTwitter_id(congressObject.getString("twitter_id"));
            congressClass.setBioguide_id(congressObject.getString("bioguide_id"));
            congressClass.setImage_url(congress_image_url_beginning + congressObject.getString("bioguide_id") + congress_image_url_end);
            if (congressObject.getString("term_end").equals("2019-01-03")){
                congressClass.setTerm_end("Next Election: 6 Nov 2018");
            }else if (congressObject.getString("term_end").equals("2017-01-03")){
                congressClass.setTerm_end("Next Election: 8 Nov 2016");
            }else{
                congressClass.setTerm_end("Next Election: 3 Nov 2020");
            }
            congressClasses[i] = congressClass;
            dataHandler.insertData_con(congressClass.getFirst_name(), congressClass.getLast_name(),
                    congressClass.getOc_email(), congressClass.getPhone(), congressClass.getBioguide_id(),
                        congressClass.getTitle(), congressClass.getTerm_end(), congressClass.getTwitter_id());
        }
        dataHandler.close();
        return congressClasses;
    }

    private ParseJSONData parseCongressDetails(String jSonData) throws JSONException {
        ParseJSONData parsejSonData = new ParseJSONData();
        parsejSonData.setCongressClasses(getCongressPeople(jSonData));  //error 3

        return parsejSonData;
    }

    private ParseJSONData parseStateDetails(String jSonData) throws JSONException {
        Log.d("FragmentDisplay", "ParseStateDetails");

        ParseJSONData parsejSonData = new ParseJSONData();
        parsejSonData.setStateLegislatorsClasses(getStatePeople(jSonData));

        return parsejSonData;
    }

    private ParseJSONData parseCouncilDetails(Politician politico) {
        ParseJSONData parsejSonData = new ParseJSONData();
        parsejSonData.setCouncilMembersClass(getCouncilPeople(politico));

        return parsejSonData;
    }


    /**
     * Sets Congressmen main listview
     */
    private void listViewCongress() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Parcelable[] parcelables = parse_jSON_data.getCongressClasses();
                mCongressClasses = Arrays.copyOf(parcelables, parcelables.length, CongressClass[].class);
                CongressListAdapter adapter = new CongressListAdapter(getActivity(), mCongressClasses);
                mListView.invalidate();
            }
        });
    }


    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {


            //        if(hidden = false);
            {
                if (mSearchView != null) {



                    mListView = (ListView) getView().findViewById(R.id.listView);
                    mSearchView = (SearchView) getView().findViewById(R.id.searchView);
                    mSearchView.setOnQueryTextListener(mOnQueryTextListener);
                    swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe_refresh);
                    fragment_list_activity_bottom_text_view = (TextView) getView().findViewById(R.id.fragment_list_activity_bottom_text_view);




                    if (mPageNumber == 0) {
//                        mSearchView.setQueryHint("Congress by location");
//                        ParseJSONData parse_stored_congress = new ParseJSONData();

//                        try {
//
//                            parse_stored_congress.setCongressClasses(getCongressFromSql());
//
//                        } catch (Exception e) {
                        }
//                        Parcelable[] parcelables_congress = parse_stored_congress.getCongressClasses();
//                        mCongressClasses = Arrays.copyOf(parcelables_congress, parcelables_congress.length, CongressClass[].class);
//                        adapter_congress = new CongressListAdapter(getContext(), mCongressClasses);


                        mListView.setAdapter(adapter_congress);
                    } else if (mPageNumber == 1) {

//                        mSearchView.setQueryHint("State Legislators by location");
//                        Log.d("FragmentDisplay", "onCreateView State Page");
//                        ParseJSONData parse_stored_state = new ParseJSONData();

//                        try {
//
//                            parse_stored_state.setStateLegislatorsClasses(getStateFromSql());
//
//                        } catch (Exception e) {
//                        }


//                        Parcelable[] parcelables_state = parse_stored_state.getStateLegislatorsClasses();
//                        mStateLegislatorsClasses = Arrays.copyOf(parcelables_state, parcelables_state.length, StateLegislatorsClass[].class);
//                        adapter_state = new StateLegislatorAdapter(getContext(), mStateLegislatorsClasses);
                        mListView.setAdapter(adapter_state);

                    } else if (mPageNumber == 2) {

//                        Log.d("FragmentDisplay", "onCreateView() mPageNumber == 2");
//                        mSearchView.setQueryHint("Council Member by location");
//                        ParseJSONData parse_stored_council = new ParseJSONData();
//
//
//                        try {
//
//                            parse_stored_council.setCouncilMembersClass(getCouncilFromSql());
//
//                        } catch (Exception e) {
//                        }
//
//                        Parcelable[] parcelables_council = parse_stored_council.getCouncilMembersClasses();
//                        mCouncilMembersClasses = Arrays.copyOf(parcelables_council, parcelables_council.length, CouncilMembersClass[].class);
//                        adapter_council = new CouncilMemberAdapter(getContext(), mCouncilMembersClasses);
                        mListView.setAdapter(adapter_council);

                    }
                }
            }
        }


    /**
     * Sets state senate Listview
     */
    private void listViewState() {
        Log.d("FragmentDisplay", "listViewState");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("FragmentDisplay", "listViewState run()");

                Parcelable[] parcelables = parse_jSON_data.getStateLegislatorsClasses();
                mStateLegislatorsClasses = Arrays.copyOf(parcelables, parcelables.length, StateLegislatorsClass[].class);
                StateLegislatorAdapter adapter = new StateLegislatorAdapter(getActivity(), mStateLegislatorsClasses);
                mListView.invalidate();
            }
        });
    }

    /**
     * Sets Councilmember's listview
     */
    private void listViewCouncil() {
        Log.d("FragmentDisplay", "listViewCouncil");

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("FragmentDisplay", "listViewCouncil run()");
                Parcelable[] parcelables = parse_jSON_data.getCouncilMembersClasses();
                mCouncilMembersClasses = Arrays.copyOf(parcelables, parcelables.length, CouncilMembersClass[].class);
                CouncilMemberAdapter adapter = new CouncilMemberAdapter(getActivity(), mCouncilMembersClasses);
                mListView.invalidate();
            }
        });
    }

    /**
     *
     * Gets State Legislators information from http API
     *
     */
    public class StateMyAsyncTask extends AsyncTask<String, String, String> {

        Context context;

        StateMyAsyncTask(Context mContext) {
            context = mContext;
            Log.d("FragmentDisplay", "stateAsyncTask constructor");
        }

        @Override
        protected String doInBackground(String... params) {
            try{
                Log.d("FragmentDisplay", "stateAsyncTask doInBackground");
                url = new URL (state_url);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {

                    StringBuilder sb = new StringBuilder();
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        Log.d("    inputLine", inputLine);
                        sb.append(inputLine).append("\n");
                    }
                    in.close();
                    Log.d("     sb.toString()", sb.toString());
                    return sb.toString();

                } finally {
                    urlConnection.disconnect();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingText();
        }

        @Override
        protected void onPostExecute (String result){
            try {

               if (result != null){
                    parse_jSON_data = parseStateDetails(result);
                    getStatePeople(result);
                    if(mPageNumber == 1 ) listViewState();
                    mListView.setVisibility(View.VISIBLE);
                    voiceText();
                }else{
                    fragment_list_activity_bottom_text_view.setText(R.string.no_wifi_connection);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            stopPullRefresh();
            if(stateSwipe == true){
                mSearchView.setQuery("",true);
                stateSwipe = false;
            }
        }
    }


    /**
     *  Converts database result to StateLegistlatorsClasses
     *
     *
     * @param result
     * @return
     * @throws JSONException
     */
    //TODO copy this for council
    private StateLegislatorsClass[] getStatePeople(String result) throws JSONException {

        Log.d("FragmentDisplay", "getStatePeople");
        JSONArray state_array = new JSONArray(result);
        StateLegislatorsClass[] stateLegislatorsClasses = new StateLegislatorsClass[state_array.length()];
        try {
            dataHandler.open(); // error 2
            dataHandler.db.execSQL("DELETE FROM stateTable");
            for (int i = 0; i < state_array.length(); i++) {
                JSONObject state_legislators_object = state_array.getJSONObject(i);
                JSONArray offices_array = state_legislators_object.getJSONArray("offices");
                StateLegislatorsClass stateLegislatorsClass = new StateLegislatorsClass();
                stateLegislatorsClass.setFull_name(state_legislators_object.getString("full_name"));
                stateLegislatorsClass.setPhone(offices_array.getJSONObject(0).optString("phone", "NA"));
                stateLegislatorsClass.setPhoto_url(state_legislators_object.getString("photo_url"));
                stateLegislatorsClass.setEmail(state_legislators_object.optString("email"));
                if (state_legislators_object.optString("email").contains("assembly")) {
                    stateLegislatorsClass.setDistrict(("Assembly District " + state_legislators_object.getString("district")));
                } else {
                    stateLegislatorsClass.setDistrict(("Senate District " + state_legislators_object.getString("district")));
                }
                stateLegislatorsClasses[i] = stateLegislatorsClass;
                dataHandler.insertData_state(stateLegislatorsClass.getFull_name(), stateLegislatorsClass.getEmail(),
                        stateLegislatorsClass.getPhone(), stateLegislatorsClass.getDistrict(),
                        stateLegislatorsClass.getPhoto_url());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        dataHandler.close();
        return stateLegislatorsClasses;
    }


    /**
     *  Converts database result to Council Classes
     *
     *
     * @return
     * @throws JSONException
     */

    //TODO no need for array of councilmembers, only one at a time
    private CouncilMembersClass[] getCouncilPeople(Politician politico) {

        //JSONArray council_array = new JSONArray(result);
        CouncilMembersClass[] councilMembersClasses = new CouncilMembersClass[1];

        dataHandler.open(); // error 2
        dataHandler.db.execSQL("DELETE FROM councilTable");


        JSONArray offices_array = null;
        CouncilMembersClass councilMembersClass = new CouncilMembersClass();
        councilMembersClass.setFull_name(politico.getFullName());
        councilMembersClass.setPhone(politico.getPhoneNumber());
        councilMembersClass.setPhoto_url(politico.getPicUrl());
        councilMembersClass.setEmail(politico.getEmailAddy());
        councilMembersClass.setDistrict(politico.getDistrict());

        Log.i("FragmentDisplay", "getCouncilPeople() | councilMemberClass full name: " + councilMembersClass.getFull_name());
        councilMembersClasses[0] = councilMembersClass;
        dataHandler.insertData_council(councilMembersClass.getFull_name(), councilMembersClass.getEmail(),
                councilMembersClass.getPhone(), councilMembersClass.getDistrict(),
                councilMembersClass.getPhoto_url());

        dataHandler.close();
        return councilMembersClasses;
    }
    
    public void loadingText(){
        fragment_list_activity_bottom_text_view.setText(R.string.loading_text);
    }

    public void voiceText(){
        fragment_list_activity_bottom_text_view.setText(R.string.make_your_voice_heard);
    }

    /**
     *
     * Attempts to retrieve and return Congressmen data from SQLLite database
     *
     * @return
     * @throws JSONException
     */
    public CongressClass[] getCongressFromSql() throws JSONException {
        Cursor con = dataHandler.db.rawQuery("Select * FROM conTable", null);
        con.moveToFirst();
        CongressClass[] congressClass = new CongressClass[dataHandler.returnConData().getCount()];
        for (int i = 0; i < congressClass.length; i++){
            CongressClass mCongressSql = new CongressClass();
                mCongressSql.setFirst_name(con.getString(con.getColumnIndexOrThrow("first_name")));
                mCongressSql.setLast_name(con.getString(con.getColumnIndexOrThrow("last_name")));
                mCongressSql.setOc_email(con.getString(con.getColumnIndexOrThrow("oc_email")));
                mCongressSql.setTitle(con.getString(con.getColumnIndexOrThrow("title")));
                mCongressSql.setPhone(con.getString(con.getColumnIndexOrThrow("phone")));
                mCongressSql.setTwitter_id(con.getString(con.getColumnIndexOrThrow("twitter_id")));
                mCongressSql.setBioguide_id(con.getString(con.getColumnIndexOrThrow("bioguide_id")));
                mCongressSql.setTerm_end(con.getString(con.getColumnIndexOrThrow("term_end")));
                mCongressSql.setImage_url(congress_image_url_beginning + con.getString(con.getColumnIndexOrThrow("bioguide_id")) + congress_image_url_end);

                congressClass[i] = mCongressSql;
                con.moveToNext();
        }
        con.close();
        return congressClass;
    }

    /**
     *
     * Attempts to retrieve and return State data from SQLLite database
     *
     * @return
     * @throws JSONException
     */
    public StateLegislatorsClass[] getStateFromSql() throws JSONException {
        Log.d("FragmentDisplay", "getStateFromSql");

        Cursor state = dataHandler.db.rawQuery("Select * FROM stateTable", null);
        state.moveToFirst();
        StateLegislatorsClass[] stateLegislatorsClasses = new StateLegislatorsClass[dataHandler.returnStateData().getCount()];
        try {
            for (int i = 0; i < stateLegislatorsClasses.length; i++) {
                StateLegislatorsClass stateLegislatorsClass = new StateLegislatorsClass();
                stateLegislatorsClass.setFull_name(state.getString(state.getColumnIndexOrThrow("full_name")));
                stateLegislatorsClass.setPhone(state.getString(state.getColumnIndexOrThrow("phone")));
                stateLegislatorsClass.setDistrict(state.getString(state.getColumnIndexOrThrow("district")));
                stateLegislatorsClass.setPhoto_url(state.getString(state.getColumnIndexOrThrow("photo_url")));
                stateLegislatorsClass.setEmail(state.getString(state.getColumnIndexOrThrow("email")));

                stateLegislatorsClasses[i] = stateLegislatorsClass;
                state.moveToNext();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        state.close();
        return stateLegislatorsClasses;
    }

    /**
     *
     * Attempts to retrieve and return Councilmember data from SQLLite database
     *
     * @return
     * @throws JSONException
     */
    public CouncilMembersClass[] getCouncilFromSql() throws JSONException {
        Log.d("FragmentDisplay", "getCouncilFromSql");

        Cursor council = dataHandler.db.rawQuery("Select * FROM councilTable", null);
        council.moveToFirst();
        CouncilMembersClass[] councilMembersClasses = new CouncilMembersClass[dataHandler.returnCouncilData().getCount()];
        try {
            for (int i = 0; i < councilMembersClasses.length; i++) {
                CouncilMembersClass councilMembersClass = new CouncilMembersClass();
                councilMembersClass.setFull_name(council.getString(council.getColumnIndexOrThrow("full_name")));
                councilMembersClass.setPhone(council.getString(council.getColumnIndexOrThrow("phone")));
                councilMembersClass.setDistrict(council.getString(council.getColumnIndexOrThrow("district")));
                councilMembersClass.setPhoto_url(council.getString(council.getColumnIndexOrThrow("photo_url")));
                councilMembersClass.setEmail(council.getString(council.getColumnIndexOrThrow("email")));

                councilMembersClasses[i] = councilMembersClass;
                council.moveToNext();
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        council.close();
        return councilMembersClasses;
    }


    /**
     *
     * Attempts to get Council info from Online Resource
     *
     *
     */
    public class AsyncGetCouncilInfo extends AsyncTask<Object, Void, Integer> {

        static final String TAG = "Async";

        public AsyncGetCouncilInfo() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            Log.d("FragmentDisplay", "AsyncGetCouncilInfo doInBackground");

            String address = (String) params[0];
            int borough = (int) params[1];

            Log.d("execute", "0: " + params[0] + "1: " + params[1]);

            return (Integer) getDistrict("http://legistar.council.nyc.gov/redirect.aspx",address,borough);
        }

        @Override
        protected void onPostExecute(Integer district) {
            //TODO check if running in right order
            dataReady(district);
            stopPullRefresh();
            super.onPostExecute(district);
        }

        public int getDistrict(String url, String address, int borough)  {

            String result = "";

            HashMap<String, String> params = new HashMap<String, String>();

            try {

                byte[] values = ("lookup_address=" + address + "&lookup_borough=" + borough).getBytes("UTF-8");

                URL councilURL = new URL(url);

                if ((url == null) || (url.equals(""))) {
                    throw new IllegalArgumentException("Url is null or empty!");
                }

                HttpURLConnection poster = (HttpURLConnection) councilURL.openConnection();

                poster.setConnectTimeout(5000);
                poster.setReadTimeout(5000);

                poster.setRequestMethod("POST");
                poster.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                poster.setDoOutput(true);
                poster.getOutputStream().write(values);

                BufferedReader inputStream;
                String str;
                inputStream = new BufferedReader(new InputStreamReader(poster.getInputStream(), "UTF-8"));

                while ((str = inputStream.readLine()) != null) {
                    //Log.d("async", str);
                    result += str;
                }

            } catch (IOException e) {

            }

            int breadCrumb = result.indexOf("District");
            Log.d("1async", "address: " + address + " borough: " + borough);

            if(breadCrumb != -1) {

                Pattern intPattern = Pattern.compile("(\\d+)");
                Matcher districtRaw = intPattern.matcher(result.substring(breadCrumb + 9, breadCrumb + 11).trim());

                if(districtRaw.find()) {

                    String district = districtRaw.group(1);
                    return Integer.parseInt(district.trim());
                }
            }

            return -1;
        }

        ArrayList<String> defaultDistrict(String message){

            ArrayList<String> data = null;

            return data;
        }
    }

///TODO figure out what's going on here
    public class AsyncGetCouncilInfoFromLatLon extends AsyncTask<Object, Void, Integer> {

        static final String TAG = "Async";

        public AsyncGetCouncilInfoFromLatLon() {
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(Object... params) {
            Log.d("FragmentDisplay", "AsyncGetCouncilInfo doInBackground");

            String address = (String) params[0];
            int borough = (int) params[1];

            Log.d("execute", "0: " + params[0] + "1: " + params[1]);

            return (Integer) getDistrict("http://legistar.council.nyc.gov/redirect.aspx",address,borough);
        }

        @Override
        protected void onPostExecute(Integer district) {
            //TODO check if running in right order
            dataReady(district);
            stopPullRefresh();
            super.onPostExecute(district);
        }

        public int getDistrict(String url, String address, int borough)  {

            String result = "";

            HashMap<String, String> params = new HashMap<String, String>();

            try {

                byte[] values = ("lookup_address=" + address + "&lookup_borough=" + borough).getBytes("UTF-8");

                URL councilURL = new URL(url);

                if ((url == null) || (url.equals(""))) {
                    throw new IllegalArgumentException("Url is null or empty!");
                }

                HttpURLConnection poster = (HttpURLConnection) councilURL.openConnection();

                poster.setConnectTimeout(5000);
                poster.setReadTimeout(5000);

                poster.setRequestMethod("POST");
                poster.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                poster.setDoOutput(true);
                poster.getOutputStream().write(values);

                BufferedReader inputStream;
                String str;
                inputStream = new BufferedReader(new InputStreamReader(poster.getInputStream(), "UTF-8"));

                while ((str = inputStream.readLine()) != null) {
                    //Log.d("async", str);
                    result += str;
                }

            } catch (IOException e) {

            }

            int breadCrumb = result.indexOf("District");
            Log.d("1async", "address: " + address + " borough: " + borough);

            if(breadCrumb != -1) {

                Pattern intPattern = Pattern.compile("(\\d+)");
                Matcher districtRaw = intPattern.matcher(result.substring(breadCrumb + 9, breadCrumb + 11).trim());

                if(districtRaw.find()) {

                    String district = districtRaw.group(1);
                    return Integer.parseInt(district.trim());
                }
            }

            return -1;
        }

        ArrayList<String> defaultDistrict(String message){

            ArrayList<String> data = null;

            return data;
        }
    }



}