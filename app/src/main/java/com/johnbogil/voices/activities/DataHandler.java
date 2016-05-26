package com.johnbogil.voices.activities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataHandler {

    DataBaseHelper dbhelper;
    Context context;
    SQLiteDatabase db;

    public static final String CON_TABLE = "conTable";  // table name
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String TITLE = "title";
    public static final String CON_EMAIL = "oc_email";
    public static final String CON_PHONE = "phone";
    public static final String TERM_END = "term_end";
    public static final String TWITTER = "twitter_id";
    public static final String BIOGUIDE_ID = "bioguide_id";

    public static final String STATE_TABLE  = "stateTable";  // table name
    public static final String STATE_NAME = "full_name";
    public static final String STATE_PHONE = "phone";
    public static final String DISTRICT = "district";
    public static final String PHOTO_URL = "photo_url";
    public static final String STATE_EMAIL = "email";

    public static final String COUNCIL_TABLE  = "councilTable";  // table name
    public static final String COUNCIL_NAME = "full_name";
    public static final String COUNCIL_PHONE = "phone";
    public static final String COUNCIL_DISTRICT = "district";
    public static final String COUNCIL_PHOTO_URL = "photo_url";
    public static final String COUNCIL_EMAIL = "email";
    

    public static final String DATA_BASE_NAME = "voices";
    public static final int DATABASE_VERSION = 16; //GC

    public static final String CREATE_CON_TABLE = "create table conTable (first_name text not null,last_name text not null,title text not null," +
                                                "oc_email text not null,phone text not null,term_end text not null,twitter_id text not null,bioguide_id text not null );";

    public static final String CREATE_STATE_TABLE = "create table stateTable (full_name text not null, phone text not null, district text not null," +
            "photo_url text not null, email text not null);";

    public static final String CREATE_COUNCIL_TABLE = "create table councilTable (full_name text not null, phone text not null, district text not null," +
            "photo_url text not null, email text not null);";

    public DataHandler(Context ctx) {
        this.context = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    //A SQLiteOpenHelper class to manage database creation and version management.
    private static class DataBaseHelper extends SQLiteOpenHelper {

        public DataBaseHelper(Context ctx) {
            super(ctx, DATA_BASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                Log.d("DataHandler", "FragmentDisplay DH");

                db.execSQL(CREATE_CON_TABLE);
                db.execSQL(CREATE_STATE_TABLE);
                db.execSQL(CREATE_COUNCIL_TABLE);

            } catch (SQLException e) { e.printStackTrace(); }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS conTable ");
            db.execSQL("DROP TABLE IF EXISTS stateTable ");
            db.execSQL("DROP TABLE IF EXISTS councilTable ");

            Log.d("DataHandler", "FragmentDisplay DH");

            onCreate(db);
        }
    }

    public DataHandler open() {
        //Create and/or open a database that will be used for reading and writing.
        db = dbhelper.getWritableDatabase();  // error
        return this;
    }

    // Close the helper class
    public void close() {
        dbhelper.close();
    }

    public long insertData_con(String first_name, String last_name, String oc_email, String phone, String bioguide_id,String title, String term_end, String twitter_id) {
        ContentValues content = new ContentValues();
        content.put(FIRST_NAME, first_name);
        content.put(LAST_NAME, last_name);
        content.put(CON_EMAIL, oc_email);
        content.put(CON_PHONE, phone);
        content.put(TITLE, title);
        content.put(TERM_END, term_end);
        content.put(BIOGUIDE_ID, bioguide_id);
        content.put(TWITTER, twitter_id);
        return db.insertOrThrow(CON_TABLE, null, content);
    }

    public long insertData_state(String full_name, String email, String phone, String district, String photo_url) {
        ContentValues content = new ContentValues();
        content.put(STATE_NAME, full_name);
        content.put(STATE_PHONE, phone);
        content.put(DISTRICT, district);
        content.put(PHOTO_URL, photo_url);
        content.put(STATE_EMAIL, email);

        return db.insertOrThrow(STATE_TABLE, null, content);
    }

    public long insertData_council(String full_name, String email, String phone, String district, String photo_url) {
        ContentValues content = new ContentValues();
        content.put(COUNCIL_NAME, full_name);
        content.put(COUNCIL_PHONE, phone);
        content.put(COUNCIL_DISTRICT, district);
        content.put(COUNCIL_PHOTO_URL, photo_url);
        content.put(COUNCIL_EMAIL, email);

        return db.insertOrThrow(COUNCIL_TABLE, null, content);
    }

    public Cursor returnConData() {
        return db.query(CON_TABLE, new String[]{FIRST_NAME, LAST_NAME, CON_EMAIL, CON_PHONE, TITLE, TERM_END, BIOGUIDE_ID, TWITTER}, null, null, null, null, null);
    }
    public Cursor returnStateData(){
        return db.query(STATE_TABLE, new String[] { STATE_NAME,STATE_PHONE,DISTRICT,PHOTO_URL,STATE_EMAIL}, null, null, null, null, null);
    }

    public Cursor returnCouncilData(){
        return db.query(COUNCIL_TABLE, new String[] { COUNCIL_NAME,COUNCIL_PHONE,DISTRICT,PHOTO_URL,COUNCIL_EMAIL}, null, null, null, null, null);
    }
}
