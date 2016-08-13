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
import com.mobilonix.voices.groups.GroupManager;
import com.mobilonix.voices.groups.model.Action;
import com.mobilonix.voices.groups.model.Group;
import com.mobilonix.voices.groups.model.Policy;
import com.mobilonix.voices.notifications.NotificationManager;

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
import java.util.LinkedHashSet;
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

    public void signIn() {

        final FirebaseAuth firebase = FirebaseAuth.getInstance();

        Log.e(TAG, "FINGERPRINT: " + getCertificateSHA1Fingerprint());

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
            }
        });
    }

    public void addUserToDatabase(final String userId) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", userId);

        database.getReference("users/" + userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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

                            GeneralUtil.toast("Total groups before removal: " + groups);

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

                                    GeneralUtil.toast("Total groups after removal: " + dataSnapshot
                                            .child(currentUserToken)
                                            .child("groups")
                                            .getValue());
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

                /* Fetch all groups accross all available groups to subscribe to  */
                ArrayList<Group> allGroups = new ArrayList<>();
                for (DataSnapshot group : dataSnapshot.getChildren()) {

                    String groupKey = group.getKey();

                    String description = group.child("description").getValue().toString();
                    String imageUrl = group.child("imageURL").getValue().toString();
                    String name = group.child("name").getValue().toString();
                    String groupTyle = group.child("groupType").getValue().toString();

                    HashMap<String, String> policyMap = (HashMap) group.child("policyPositions").getValue();
                    ArrayList<Policy> policies = new ArrayList<>();
                    if(policyMap != null) {
                        Iterator it = policyMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            policies.add(new Policy((String) pair.getKey(), (String) pair.getValue(), ""));
                            it.remove();
                        }
                    }

                    HashMap<String, String> actionMap = (HashMap) group.child("actions").getValue();
                    ArrayList<String> actions = new ArrayList<>();
                    if(actionMap != null) {

                        Iterator actionIt = actionMap.entrySet().iterator();
                        while (actionIt.hasNext()) {
                            Map.Entry pair = (Map.Entry) actionIt.next();
                            actions.add((String) pair.getKey());
                            actionIt.remove();
                        }
                    }

                    Group groupToAdd = new Group(name, groupTyle, description, imageUrl, "", policies, actions, groupKey);
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

                Log.e("SESSION", "List of All groups");
                for (Group group : allGroups) {
                    Log.e("SESSION", "Group name: " + group.getGroupKey());
                }

                /* TODO: Improve this algorithm here.  It's linear, can be optimized with Hash Structures */
                for (DataSnapshot user : dataSnapshot.getChildren()) {

                    String dummyToken = "";

                    if (currentUserToken.equals(user.getKey()) || user.getKey().equals(dummyToken)) {

                        if (user.child("groups").exists()) {

                            HashMap<String, String> groupMap
                                    = (HashMap) user.child("groups").getValue();

                            Log.e(TAG, "User: " + user.getKey().toString() + "has groups " + groupMap);

                            Iterator it = groupMap.entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry pair = (Map.Entry) it.next();
                                for (Group group : allGroups) {
                                    if (pair.getKey().equals(group.getGroupKey())) {
                                        userGroups.add(group);
                                        GroupManager.INSTANCE.subscribeToGroup(group, false);
                                    }
                                }
                                it.remove();
                            }

                        } else {
                            GeneralUtil.toast("User " + currentUserToken + " has no groups!");
                        }
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
                    String body = (String) action.child("body").getValue();
                    String groupKey = (String) action.child("groupKey").getValue();
                    String groupName = (String) action.child("groupName").getValue();
                    String imageUrl = (String) action.child("imageURL").getValue();
                    String subject = (String) action.child("subject").getValue();
                    String timestamp = action.child("timestamp").toString();
                    String title = (String) action.child("title").getValue();

                    allActions.add(new Action(action.getKey(),
                            (String) action.child("body").getValue(),
                            (String) action.child("groupKey").getValue(),
                            (String) action.child("groupName").getValue(),
                            (String) action.child("imageUrl").getValue(),
                            (String) action.child("subject").getValue(),
                            action.child("timestamp").toString(),
                            (String) action.child("title").getValue()));

                    Action actionToAdd =
                            new Action(actionKey,
                                    body,
                                    groupKey,
                                    groupName,
                                    imageUrl,
                                    subject,
                                    timestamp,
                                    title);

                    allActions.add(actionToAdd);
                }

                callback.onExecuted(allActions);
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

    public boolean checkIfFirstRun() {
        SharedPreferences preferences
                = PreferenceManager.getDefaultSharedPreferences(VoicesApplication.getContext());

        if(preferences.getBoolean(CHECK_IF_FIRST_RUN, true)) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(CHECK_IF_FIRST_RUN, false);
            editor.commit();

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
