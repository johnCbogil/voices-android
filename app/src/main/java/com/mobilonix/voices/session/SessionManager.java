package com.mobilonix.voices.session;


import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobilonix.voices.R;

import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.base.util.GeneralUtil;
import com.mobilonix.voices.delegates.Callback;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;

public enum SessionManager {

    INSTANCE;

    String currentToken = FIREBASE_NO_TOKEN;

    private static final String FIREBASE_TOKEN_KEY = "FIREBASE_TOKEN_KEY";
    private static final String FIREBASE_NO_TOKEN = "FIREBASE_NO_TOKEN";

    private String currentNotificationToken;

    public void setCurrentNotificationToken(String currentNotificationToken) {
        this.currentNotificationToken = currentNotificationToken;
    }

    public String getCurrentNotificationToken() {
        return currentNotificationToken;
    }

    public void signIn() {

        final FirebaseAuth firebase = FirebaseAuth.getInstance();

        Log.e("SESSIONS", "FINGERPRINT: " + getCertificateSHA1Fingerprint());

        currentToken = PreferenceManager
                .getDefaultSharedPreferences(VoicesApplication.getContext())
                .getString(FIREBASE_TOKEN_KEY, FIREBASE_NO_TOKEN);

        firebase.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    currentToken = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(VoicesApplication.getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(FIREBASE_TOKEN_KEY, currentToken);
                    editor.commit();

                    Log.e("SESSION", "Firebase authenticated successfully with token:" + currentToken);
                    GeneralUtil.toast("Firebase authenticated successfully with token:" + currentToken);

                    addUserToDatabase(currentToken);

                } else {
                    GeneralUtil
                            .toast("There was an error authenticating with the device. " +
                                    "Please restart the app and make sure your network connection is on" +
                                    task.getException().getMessage());

                    Log.e("SESSION ERROR: ", task.getException().getMessage());

                }
            }
        });
    }

    public void fetchUsersFromDatabase(Callback<String> callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("users");

        // Attach a listener to read the data at our posts reference
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GeneralUtil.toast("Firebase data successful");
                Log.e("FIREBASE DATABASE", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                GeneralUtil.toast("Firebase data READ FAILED!");
                Log.e("FIREBASE DATABASE", databaseError.toString());
            }
        });

    }

    public void addUserToDatabase(final String userId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        database.getReference("users/" + userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    GeneralUtil.toast("User " + userId + " already exists in the database!");
                } else {
                    database.getReference("users").child(userId).child("userId").setValue(userId)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    GeneralUtil.toast("Added user successfully");
                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public void fetchPoliciesFromDatabase(String group, Callback<String> callback) {

    }


    public void fetchFollowersFromDatabase(String group, Callback<String> callback) {

    }

    public void fetchGroupsFromDatabase(Callback<Group> callback) {

    }

    public void fetchActionsFromDatabase(Callback<Group> callback) {

    }


    private String getCertificateSHA1Fingerprint() {
        PackageManager pm = VoicesApplication.getContext().getPackageManager();
        String packageName = VoicesApplication.getContext().getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }

}
