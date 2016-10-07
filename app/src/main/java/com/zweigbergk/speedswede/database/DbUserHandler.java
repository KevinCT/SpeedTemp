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
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.ProductLock;

enum DbUserHandler {
    INSTANCE;

    public static final String TAG = DbUserHandler.class.getSimpleName().toUpperCase();

    private DatabaseReference mRoot;

    private UserPoolListener mUserPoolListener;

    private User mLoggedInUser;

    public static DbUserHandler getInstance() {
        return INSTANCE;
    }


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
        mUserPoolListener = new UserPoolListener();

        DatabaseReference ref = mRoot.child(Constants.POOL);
        ref.addChildEventListener(mUserPoolListener);
        Log.d(TAG, "Connecting pool listener...");
        ref.keepSynced(true);
    }

    User convertToUser(DataSnapshot snapshot) {
        return new UserProfile(snapshot.child("displayName").getValue().toString(),
                snapshot.child("uid").getValue().toString());
    }

    ProductBuilder<User> getUserById(String uid) {
        ProductBuilder<User> builder = new ProductBuilder<>(items -> {
            String name = (String) items.get(ProductLock.NAME);
            String id = (String) items.get(ProductLock.ID);
            return new UserProfile(name, id);
        });
        builder.attachLocks(ProductLock.NAME, ProductLock.ID);

        if(uid != null) {
            mRoot.child(Constants.USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    builder.addItem(ProductLock.NAME,
                            dataSnapshot.child(Constants.DISPLAY_NAME).getValue());
                    builder.addItem(ProductLock.ID,
                            dataSnapshot.child(Constants.USER_ID).getValue());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        return builder;
    }

    void addUserToPool(User user) {
        mRoot.child(Constants.POOL).child(user.getUid()).setValue(user);
    }

    void pushUser(User user) {
        mRoot.child(Constants.USERS).child(user.getUid()).setValue(user);
    }


    void removeUserFromPool(User user) {
        mRoot.child(Constants.POOL).child(user.getUid()).setValue(null);
    }

    String getActiveUserId() {
        if (getActiveUser() != null) {
            return getActiveUser().getUid();
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

    User getActiveUser() {
        if (mLoggedInUser == null) {
            User firebaseUser = UserProfile.from(FirebaseAuth.getInstance().getCurrentUser());

            if (firebaseUser != null) {
                setLoggedInUser(firebaseUser);
            }
        }

        return mLoggedInUser;
    }

    void setLoggedInUser(User user) {
        mLoggedInUser = user;
    }

    void pushTestUser() {
        getUserById(Constants.TEST_USER_UID).then(user -> {
            if (user == null) {
                UserProfile testUserProfile = new UserProfile("TestBot", Constants.TEST_USER_UID);
                DbUserHandler.INSTANCE.pushUser(testUserProfile);
            }
        });
    }

    UserPoolListener getPoolListener() {
        return mUserPoolListener;
    }
}
