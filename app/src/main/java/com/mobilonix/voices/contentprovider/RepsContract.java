package com.mobilonix.voices.contentprovider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class RepsContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.mobilonix.voices.app";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REPS = "reps";

    /* Inner class that defines the table contents of the reps table */
    public static final class RepsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPS;

        String mPhoneNumber;
        String mEmailAddy;
        String mTwitterHandle;
        String mPicUrl;
        public static final String TABLE_NAME = "reps";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_PARTY = "party";
        public static final String COLUMN_DISTRICT = "district";
        public static final String COLUMN_TERM_END = "term_end";
        public static final String COLUMN_ELECTION_DATE = "election_date";
        public static final String COLUMN_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_EMAIL_ADDRESS = "email_address";
        public static final String COLUMN_TWITTER_HANDLE = "twitter_handle";
        public static final String COLUMN_BITMAP = "bitmap";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
