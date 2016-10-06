package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.Client;

import java.util.List;

public enum DbUserHandler {
    INSTANCE;

    public static final String TAG = DbUserHandler.class.getSimpleName().toUpperCase();

    public static final String POOL = "pool";
    public static final String USERS = "users";

    private DatabaseReference mRoot;

    private UserPoolListener userPoolListener;

    private User mLoggedInUser;

    public void initialize() {
        mRoot = FirebaseDatabase.getInstance().getReference();

        initializeUserPoolListener();
    }

    public void logout() {
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        mLoggedInUser = null;
    }

    private void initializeUserPoolListener() {
        userPoolListener = new UserPoolListener();

        DatabaseReference ref = mRoot.child(POOL);
        ref.addChildEventListener(userPoolListener);
        ref.keepSynced(true);
    }

    public User convertToUser(DataSnapshot snapshot) {
        return new UserProfile(snapshot.child("displayName").getValue().toString(),
                snapshot.child("uid").getValue().toString());
    }

    public void getUserById(String uid, Client<User> client) {

        if(uid != null) {
            mRoot.child(USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        User user = convertToUser(dataSnapshot);

                        client.supply(user);
                    } else {
                        client.supply(null);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    public void addUserToPool(User user) {
        mRoot.child(POOL).child(user.getUid()).setValue(user);
    }

    public void pushUser(User user) {
        mRoot.child(USERS).child(user.getUid()).setValue(user);
    }


    public void removeUserFromPool(User user) {
        mRoot.child(POOL).child(user.getUid()).setValue(null);
    }

    public String getActiveUserId() {
        if (getLoggedInUser() != null) {
            return getLoggedInUser().getUid();
        }

        return getFirebaseAuthUid();
    }

    private String getFirebaseAuthUid() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String uid = profile.getUid();
                if (uid != null)
                    return uid;
            }
        }

        return null;
    }

    public User getLoggedInUser() {
        if (mLoggedInUser == null) {
            User firebaseUser = UserProfile.from(FirebaseAuth.getInstance().getCurrentUser());

            if (firebaseUser != null) {
                setLoggedInUser(firebaseUser);
            }
        }

        return mLoggedInUser;
    }

    public void setLoggedInUser(User user) {
        mLoggedInUser = user;
    }

    public void pushTestUser() {
        DbUserHandler.INSTANCE.getUserById(Constants.TEST_USER_UID, testUser -> {
            if (testUser == null) {
                UserProfile testUserProfile = new UserProfile("TestBot", Constants.TEST_USER_UID);
                DbUserHandler.INSTANCE.pushUser(testUserProfile);
            }
        });
    }

    public void addUserPoolClient(Client<DataChange<User>> client) {
        userPoolListener.addClient(client);
    }

    public void removeUserPoolClient(Client<DataChange<User>> client) {
        userPoolListener.removeClient(client);
    }
}
