package com.mobilonix.voices.session;


import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mobilonix.voices.BuildConfig;
import com.mobilonix.voices.R;
import com.mobilonix.voices.VoicesApplication;
import com.mobilonix.voices.callbacks.Callback;
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.model.Policy;
import com.mobilonix.voices.representatives.model.Representative;
import com.mobilonix.voices.util.GeneralUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public enum SessionManager {

    INSTANCE;

    private final static String TAG = SessionManager.class.getCanonicalName();

    String currentUserToken = FIREBASE_NO_TOKEN;

    private static final String FIREBASE_TOKEN_KEY = "FIREBASE_TOKEN_KEY";
    private static final String FIREBASE_NO_TOKEN = "FIREBASE_NO_TOKEN";
    private static final String CHECK_IF_FIRST_RUN = "CHECK_IF_FIRST_RUN";

    private String currentNotificationToken;

    public void setCurrentNotificationToken(String currentNotificationToken) {
        this.currentNotificationToken = currentNotificationToken;
    }

    public String getCurrentNotificationToken() {
        return currentNotificationToken;
    }

    public void signIn(final Callback<Boolean> callback) {

        final FirebaseAuth firebase = FirebaseAuth.getInstance();
        currentUserToken = PreferenceManager
                .getDefaultSharedPreferences(VoicesApplication.getContext())
                .getString(FIREBASE_TOKEN_KEY, FIREBASE_NO_TOKEN);

        firebase.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    currentUserToken = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    SharedPreferences prefs = PreferenceManager
                            .getDefaultSharedPreferences(VoicesApplication.getContext());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(FIREBASE_TOKEN_KEY, currentUserToken);
                    editor.commit();

                    addUserToDatabase(currentUserToken);

                } else {
                    GeneralUtil
                            .toast("There was an error authenticating with the device. " +
                                    "Please restart the app and make sure your network connection is on" +
                                    task.getException().getMessage());

                    Log.e("SESSION ERROR: ", task.getException().getMessage());

                }

                callback.onExecuted(true);
            }
        });

        if(BuildConfig.DEBUG) {
            GeneralUtil.toast("This is a debug build. Don't submit this to the store.  Subscribing to test topic");
            FirebaseMessaging.getInstance().subscribeToTopic("TEST");
        }
    }

    public void addUserToDatabase(final String userId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        database.getReference("users/" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                } else {
                    database.getReference("users").child(userId).child("userId").setValue(userId)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void removeGroupForCurrentUser(final Group group, final Callback<Boolean> callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(currentUserToken).child("groups").exists()) {

                            final HashMap<String, Integer> groups =
                                    (HashMap) dataSnapshot
                                            .child(currentUserToken)
                                            .child("groups")
                                            .getValue();

                            groups.remove(group.getGroupKey());

                            database.getReference("users")
                                              .child(currentUserToken)
                                              .child("groups")
                                              .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    database.getReference("users")
                                            .child(currentUserToken)
                                            .child("groups")
                                            .setValue(groups);

                                }
                            });


                        }

                        callback.onExecuted(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onExecuted(true);
                    }
                });
    }

    public void addGroupForCurrentUser(final Group group, final Callback<Boolean> callback) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(currentUserToken).child("groups").exists()) {

                            HashMap<String, Integer> groups = (HashMap) dataSnapshot.child(currentUserToken).child("groups").getValue();
                            groups.put(group.getGroupKey(), 1);
                            database.getReference()
                                    .child("users")
                                    .child(currentUserToken)
                                    .child("groups")
                                    .setValue(groups);
                            database.getReference().child("users").child(currentUserToken).child("groups").setValue(groups);

                        } else {
                            HashMap<String, Integer> groups = new HashMap<>();
                            groups.put(group.getGroupKey(), 1);
                            database.getReference()
                                    .child("users")
                                    .child(currentUserToken)
                                    .child("groups")
                                    .setValue(groups);
                        }

                        callback.onExecuted(true);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        callback.onExecuted(true);
                    }
                });
    }

    /**
     * This call obtains all the groups from the remote database
     *
     * @param allGroupsCallback
     * @param userGroupsCallback
     */
    public void fetchAllGroupsFromDatabase(final Callback<ArrayList<Group>> allGroupsCallback,
                                           final Callback<ArrayList<Group>> userGroupsCallback) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference groupsRef = database.getReference("groups");

        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /* Fetch all groups across all available groups to subscribe to  */
                ArrayList<Group> allGroups = new ArrayList<>();
                for (DataSnapshot group : dataSnapshot.getChildren()) {

                    String groupKey = group.getKey();
                    String description;
                    String imageUrl;
                    String name;
                    String groupType;
                    String website;
                    try{
                        description = group.child("description").getValue().toString();
                    } catch(NullPointerException e){
                        description = "";
                    }

                    try{
                        imageUrl = group.child("imageURL").getValue().toString();
                    } catch(NullPointerException e){
                        imageUrl = "";
                    }

                    try{
                        name = group.child("name").getValue().toString();
                    } catch(NullPointerException e){
                        name = "";
                    }

                    try {
                        groupType = group.child("groupType").getValue().toString();
                    } catch(NullPointerException e){
                        groupType="";
                    }

                    try {
                        website = group.child("website").getValue().toString();
                    } catch(NullPointerException e){
                        website = "";
                    }

                    String debug = "false";
                    if(group.child("debug").exists()) {
                        debug = group.child("debug").getValue().toString();
                    }

                    HashMap<String, String> policyMap = new HashMap<>();
                    try {
                        policyMap = (HashMap) group.child("policyPositions").getValue();
                    } catch (Exception e) {
                        policyMap = new HashMap<>();
                        Log.e(TAG, "Could not fetch action map from firebase");
                    }

                    ArrayList<Policy> policies = new ArrayList<>();
                    if(policyMap != null) {
                        Iterator it = policyMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            policies.add(new Policy((String) pair.getKey(), (String) pair.getValue(), ""));
                            it.remove();
                        }
                    }

                    HashMap<String, String> actionMap = new HashMap<>();
                    try {
                        actionMap = (HashMap) group.child("actions").getValue();
                    } catch (Exception e) {
                        actionMap = new HashMap<>();
                        Log.e(TAG, "Could not fetch policy map from firebase");
                    }

                    ArrayList<String> actions = new ArrayList<>();
                    if(actionMap != null) {

                        Iterator actionIt = actionMap.entrySet().iterator();
                        while (actionIt.hasNext()) {
                            Map.Entry pair = (Map.Entry) actionIt.next();
                            actions.add((String) pair.getKey());
                            actionIt.remove();
                        }
                    }

                    Group groupToAdd = new Group(name, groupType, description, imageUrl, "", website, policies, actions, groupKey);

                    try {
                        groupToAdd.setDebug(Boolean.parseBoolean(debug));
                    } catch (Exception e) {
                        Log.e(TAG, "Couldn't read the debug flag");
                    }

                    allGroups.add(groupToAdd);
                }


                allGroupsCallback.onExecuted(allGroups);
                fetchUserGroups(database, allGroups, userGroupsCallback);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                /* TODO: In case the response fails we want something cached.
                   The latest groups response needs to be de-serialized and added to our list  */

                ArrayList<Group> allGroups = new ArrayList<Group>();
                fetchUserGroups(database, allGroups, userGroupsCallback);
            }
        });

    }

    /**
     * This is a private method AND we pass in a database reference BECAUSE we need first the groups
     * to be added before we can add the associated groups for the current user
     *
     * @param database
     */
    private void fetchUserGroups(FirebaseDatabase database,
                                 final ArrayList<Group> allGroups,
                                 final Callback<ArrayList<Group>> userGroupsCallback) {


        final DatabaseReference ref = database.getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Group> userGroups = new ArrayList<Group>();

                /* TODO: Improve this algorithm here.  It's linear, can be optimized with Hash Structures */
                for (DataSnapshot user : dataSnapshot.getChildren()) {

                    String dummyToken = "";

                    if (currentUserToken.equals(user.getKey()) || user.getKey().equals(dummyToken)) {

                        if (user.child("groups").exists()) {

                            HashMap<String, String> groupMap
                                    = (HashMap) user.child("groups").getValue();
                            Iterator it = groupMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                for (Group group : allGroups) {
                                    if (pair.getKey().equals(group.getGroupKey())) {
                                        userGroups.add(group);
                                        GroupManager.INSTANCE.subscribeToGroup(group, false, null);
                                    }
                                }
                                it.remove();
                            }

                        } else {}
                    }

                }

                userGroupsCallback.onExecuted(userGroups);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    public void fetchAllActions(final Callback<ArrayList<Action>> callback) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference ref = database.getReference("actions");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Action> allActions = new ArrayList<Action>();

                for (DataSnapshot action : dataSnapshot.getChildren()) {
                    String actionKey = action.getKey();
                    String body;
                    String groupKey;
                    String groupName;
                    String imageUrl;
                    String actionType;
                    Representative singleRep;

                    try{
                        body = (String) action.child("body").getValue();
                    } catch(NullPointerException e){
                        body = "";
                    }

                    try{
                        groupKey = (String) action.child("groupKey").getValue();
                    } catch(NullPointerException e){
                        groupKey = "";
                    }

                    try{
                        groupName = (String) action.child("groupName").getValue();
                    } catch(NullPointerException e){
                        groupName = "";
                    }

                    try{
                        imageUrl = (String) action.child("imageURL").getValue();
                    } catch(NullPointerException e){
                        imageUrl="";
                    }

                    long level = 3;
                    if(action.child("level").exists()) {
                        level = (long) action.child("level").getValue();
                    }
                    String subject;
                    long timestamp;
                    String title;
                    String script = (String) action.child ("script").getValue();
                    if(script==null) {
                        script = VoicesApplication.getContext().getString(R.string.response_4);
                    }

                    try{
                        subject =(String) action.child("subject").getValue();
                    } catch(NullPointerException e){
                        subject="";
                    }

                    try{
                        timestamp = (long)action.child("timestamp").getValue();
                    } catch(NullPointerException e){
                        timestamp = 0;
                    }

                    try{
                        title = (String) action.child("title").getValue();
                    } catch(NullPointerException e){
                        title = "";
                    }

                    try {
                        actionType = (String) action.child("actionType").getValue();
                    } catch(NullPointerException e){
                        actionType = "";
                    }

                    try{
                        body = (String) action.child("body").getValue();
                    } catch(NullPointerException e){
                        body = "";
                    }

                    if(actionType!=null && actionType.equals("singleRep")) {
                        Map<String, Object> map = (Map<String, Object>)action.child("representative").getValue();
                        String repTitle;
                        String name;
                        String phone;
                        String twitter;
                        String email;

                        try{
                            repTitle = (String) map.get("title");
                        } catch(NullPointerException e){
                            repTitle = "";
                        }

                        try{
                            name = (String) map.get("name");
                        } catch(NullPointerException e){
                            name = "";
                        }

                        try{
                            phone = (String) map.get("phone");
                        } catch(NullPointerException e){
                            phone = "";
                        }

                        try{
                            twitter = (String) map.get("twitter");
                        } catch(NullPointerException e){
                            twitter = "";
                        }

                        try{
                            email = (String) map.get("email");
                        } catch(NullPointerException e){
                            email = "";
                        }
                        singleRep = new Representative(repTitle,
                                name,
                                null,
                                "",
                                "",
                                "",
                                phone,
                                twitter,
                                "",
                                email,
                                null,
                                "1");
                    } else {
                        singleRep=null;
                    }
                        allActions.add(new Action(actionKey,
                                body,
                                groupKey,
                                groupName,
                                imageUrl,
                                level,
                                subject,
                                timestamp,
                                title,
                                script,
                                actionType,
                                singleRep));

                        Action actionToAdd =
                                new Action(actionKey,
                                        body,
                                        groupKey,
                                        groupName,
                                        imageUrl,
                                        level,
                                        subject,
                                        timestamp,
                                        title,
                                        script,
                                        actionType,
                                        singleRep);

                        allActions.add(actionToAdd);
                        callback.onExecuted(allActions);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                /* TODO: Cache actions here */

            }
        });

    }

    /**
     * Use this to obtain the SHA finger print of the keystore. Useful for double checking the idenity
     * of the build
     *
     * @return
     */
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

    //if check_if_first_run=true and reset==true, set check_if_first_run to false and return true
    //if check_if_first_run=true and reset==false, return true
    //if check_if_first_run=false and reset==true, return false
    //if check_if_first_run=false and reset==false, return false
    //it returns true if it is the first run and false if it is not
    //the parameter indicates whether to set check_if_first_run to false or not
    public boolean isFirstRun(boolean reset) {
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(VoicesApplication.getContext());
        if(preferences.getBoolean(CHECK_IF_FIRST_RUN, true)) {
            if(reset) {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(CHECK_IF_FIRST_RUN, false);
                editor.commit();
            }
            /* Need to always commit editor changes */
            return true;
        } else {
            return false;
        }
    }

    public String getCurrentUserToken() {
        return currentUserToken;
    }
}
