package com.mobilonix.voices.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mobilonix.voices.contentprovider.RepsContract.RepsEntry;

public class RepsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "reps.db";

    public RepsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create a table to hold reps.
        final String SQL_CREATE_REPS_TABLE = "CREATE TABLE " + RepsEntry.TABLE_NAME + " (" +
                RepsEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                RepsEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_GENDER + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_PARTY + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_DISTRICT + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_TERM_END + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_ELECTION_DATE + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_PHONE_NUMBER + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_EMAIL_ADDRESS + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_TWITTER_HANDLE + " TEXT NOT NULL, " +
                RepsEntry.COLUMN_BITMAP + " TEXT NOT NULL, " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_REPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RepsContract.RepsEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}