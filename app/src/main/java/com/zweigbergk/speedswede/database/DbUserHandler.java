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
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.database.eventListener.WellBehavedUserListener;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.ProductLock;
import com.zweigbergk.speedswede.core.User.Preference;

import java.util.HashMap;
import java.util.Map;
import static com.zweigbergk.speedswede.Constants.USERS;
import static com.zweigbergk.speedswede.Constants.preference;
import static com.zweigbergk.speedswede.Constants.NOTIFICATIONS;
import static com.zweigbergk.speedswede.Constants.LANGUAGE;
import static com.zweigbergk.speedswede.Constants.SWEDISH_SKILL;
import static com.zweigbergk.speedswede.Constants.STRANGER_SWEDISH_SKILL;

enum DbUserHandler {
    INSTANCE;

    public static final String TAG = DbUserHandler.class.getSimpleName().toUpperCase();

    private DatabaseReference mRoot;

    private WellBehavedUserListener mUsersListener;
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

    // NOTE: Listening to all Users all the time is not optimal.
    // It's whatever if it doesn't cause issues.
    void registerUsersListener() {
        mUsersListener = new WellBehavedUserListener();

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

    WellBehavedUserListener getUserListener() {
        return mUsersListener;
    }

    void addUserToPool(User user) {
        mRoot.child(Constants.POOL).child(user.getUid()).setValue(user.getUid());
    }

    void pushUser(User user) {
        mRoot.child(Constants.USERS).child(user.getUid()).setValue(user);
    }


    void removeUserFromPool(User user) {
        mRoot.child(Constants.POOL).child(user.getUid()).setValue(null);
    }

    void setUserAttribute(User user, UserManipulator.UserAttribute attribute, Object value) {
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

    UserPoolListener getPoolListener() {
        return mUserPoolListener;
    }

    ProductBuilder<User> getUser(String uid) {
        ProductBuilder<User> builder = new ProductBuilder<>(userBlueprint);

        builder.attachLocks(ProductLock.NAME, ProductLock.ID, ProductLock.NOTIFICATIONS,
                ProductLock.LANGUAGE, ProductLock.SWEDISH_SKILL, ProductLock.STRANGER_SWEDISH_SKILL);

        if(uid != null) {
            mRoot.child(Constants.USERS).child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            builder.addItem(ProductLock.NAME,
                                    dataSnapshot.child(Constants.DISPLAY_NAME).getValue());

                            builder.addItem(ProductLock.ID,
                                    dataSnapshot.child(Constants.USER_ID).getValue());

                            builder.addItem(ProductLock.NOTIFICATIONS,
                                    dataSnapshot.child(preference(NOTIFICATIONS)).getValue());

                            builder.addItem(ProductLock.LANGUAGE,
                                    dataSnapshot.child(preference(LANGUAGE)).getValue());

                            builder.addItem(ProductLock.SWEDISH_SKILL,
                                    dataSnapshot.child(preference(SWEDISH_SKILL)).getValue());

                            builder.addItem(ProductLock.STRANGER_SWEDISH_SKILL,
                                    dataSnapshot.child(preference(STRANGER_SWEDISH_SKILL)).getValue());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
        }

        return builder;
    }

    private static final ProductBuilder.Blueprint<User> userBlueprint = items -> {
        String name = items.getString(ProductLock.NAME);
        String id = items.getString(ProductLock.ID);

        Map<User.Preference, Object> preferences = new HashMap<>();
        preferences.put(Preference.NOTIFICATIONS, items.getBoolean(ProductLock.NOTIFICATIONS));
        preferences.put(Preference.LANGUAGE, items.getString(ProductLock.LANGUAGE));
        preferences.put(Preference.SWEDISH_SKILL, items.getLong(ProductLock.SWEDISH_SKILL));
        preferences.put(Preference.STRANGER_SWEDISH_SKILL, items.getLong(ProductLock.STRANGER_SWEDISH_SKILL));

        return new UserProfile(name, id).withPreferences(preferences);
    };
}
