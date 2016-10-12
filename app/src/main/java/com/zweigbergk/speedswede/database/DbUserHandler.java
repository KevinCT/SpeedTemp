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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.eventListener.UserListener;
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.Statement;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.UserFactory;


import static com.zweigbergk.speedswede.Constants.POOL;
import static com.zweigbergk.speedswede.Constants.USERS;
import static com.zweigbergk.speedswede.Constants.BANS;
import static com.zweigbergk.speedswede.Constants.BANLIST;

enum DbUserHandler {
    INSTANCE;

    public static final String TAG = DbUserHandler.class.getSimpleName().toUpperCase();

    private DatabaseReference mRoot;

    private UserListener mUsersListener;
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

    // NOTE: Listening to all Users all the time is invert optimal.
    // It's whatever if it doesn't cause issues.
    void registerUsersListener() {
        mUsersListener = new UserListener();

        Query userRef = mRoot.child(USERS);
        userRef.keepSynced(true);
        userRef.addChildEventListener(mUsersListener);
    }

    private void initializeUserPoolListener() {
        mUserPoolListener = new UserPoolListener();

        DatabaseReference ref = mRoot.child(Constants.POOL);
        ref.addChildEventListener(mUserPoolListener);
        Log.d(TAG, "Connecting pool listener...");
        ref.keepSynced(true);
    }

    UserListener getUserListener() {
        return mUsersListener;
    }

    public Statement exists(User user) {
        return hasReference(mRoot.child(USERS).child(user.getUid()));
    }

    public Statement isInPool(User user) {
        return hasReference(mRoot.child(POOL).child(user.getUid()));
    }

    void addUserToPool(User user) {
        mRoot.child(Constants.POOL).child(user.getUid()).setValue(true);
    }

    void pushUser(User user) {
        mRoot.child(Constants.USERS).child(user.getUid()).setValue(user);
    }


    void removeUserFromPool(User user) {
        mRoot.child(Constants.POOL).child(user.getUid()).setValue(null);
    }

    void setUserAttribute(User user, UserReference.UserAttribute attribute, Object value) {
        String key = attribute.getDbKey();

        mRoot.child(USERS).child(user.getUid()).child(key).setValue(value);
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
        getUser(Constants.TEST_USER_UID).then(user -> {
            if (user == null) {
                UserProfile testUserProfile = new UserProfile("TestBot", Constants.TEST_USER_UID);
                DbUserHandler.INSTANCE.pushUser(testUserProfile);
            }
        });
    }

    public ProductBuilder<User> getUser(String uid) {
        final ProductBuilder<User> builder = ProductBuilder.shell();

        if(uid != null) {
            mRoot.child(Constants.USERS).child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserFactory.buildUser(builder, dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            builder.setBuildFailed(true);
                        }
                    });
        }

        return builder;
    }

    Statement userExists(User user) {
        return hasReference(mRoot.child(USERS).child(user.getUid()));
    }

    Statement userExists(String userId) {
        return hasReference(mRoot.child(USERS).child(userId));
    }

    Statement isInUserPool(User user) {
        return hasReference(mRoot.child(POOL).child(user.getUid()));
    }

    Statement isInUserPool(String userId) {
        return hasReference(mRoot.child(POOL).child(userId));
    }

    /*private ProductBuilder<Boolean> nodeHasUser(DatabaseReference reference, String userId) {
        ProductBuilder<Boolean> builder =
                new ProductBuilder<>(items -> items.getBoolean(ProductLock.ASSERTION));

        builder.attachLocks(ProductLock.ASSERTION);

        reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                builder.addItem(ProductLock.ASSERTION, dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                builder.setBuildFailed(true);
            }
        });

        return builder;
    }*/

    UserPoolListener getPoolListener() {
        return mUserPoolListener;
    }

    public Statement hasReference(DatabaseReference ref) {
        Statement builder = new Statement();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                builder.setReturnValue(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                builder.setBuildFailed(true);
            }
        });

        return builder;
    }

     Statement isActiveUserBlockedBy(User user) {
        DatabaseReference ref = mRoot.child(BANS).child(user.getUid()).child(BANLIST).child(getActiveUserId());
        return DbUserHandler.getInstance().hasReference(ref);
    }
}
