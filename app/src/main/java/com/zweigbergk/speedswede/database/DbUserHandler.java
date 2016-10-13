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
import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.eventListener.UserListener;
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.async.GoodStatement;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.factory.UserFactory;
import com.zweigbergk.speedswede.util.methodwrapper.StateRequirement;


import java.util.ArrayList;
import java.util.List;

import static com.zweigbergk.speedswede.Constants.POOL;
import static com.zweigbergk.speedswede.Constants.USERS;
import static com.zweigbergk.speedswede.Constants.BANS;
import static com.zweigbergk.speedswede.Constants.BANLIST;
<<<<<<< fefe8094ae1b04825317f42cdba88f64249c48f3
=======
import static com.zweigbergk.speedswede.util.async.PromiseNeed.USER_ID_LIST;
>>>>>>> Restructure async helper functions system

class DbUserHandler extends DbHandler {
    private static DbUserHandler INSTANCE;

    public static final String TAG = DbUserHandler.class.getSimpleName().toUpperCase();

    private DatabaseReference mRoot;

    private UserListener mUsersListener;
    private UserPoolListener mUserPoolListener;

    private User mLoggedInUser;

    private DbUserHandler() {

    }

    public static DbUserHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbUserHandler();
        }

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

    public Promise<User> getUser(String uid) {
        final Promise<User> promise = Promise.create();

        if(uid != null) {
            mRoot.child(Constants.USERS).child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserFactory.buildUser(promise, dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            promise.setPromiseFailed(true);
                        }
                    });
        }

        return promise;
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

    UserPoolListener getPoolListener() {
        return mUserPoolListener;
    }

    public void liftBlock(String strangerUid) {
        String uid = getActiveUserId();
        DatabaseReference ref = mRoot.child(BANS).child(uid).child(BANLIST).child(strangerUid);
        delete(ref);
    }

    public Promise<Banner> getBans(String uid){
        Promise<Banner> promise = Promise.create();
        promise.requires(USER_ID_LIST);

        mRoot.child(BANS).child(uid).child(BANLIST).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> blockedList = new ArrayList<>();

                StateRequirement hasAllIds = list -> ((List<String>)list).size() == dataSnapshot.getChildrenCount();
                promise.requireState(USER_ID_LIST, hasAllIds);
                promise.addItem(USER_ID_LIST, blockedList);

                Lists.forEach(dataSnapshot.getChildren(), userEntry -> {
                    String userId = userEntry.getKey();
                    blockedList.add(userId);
                });

                promise.remind();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        return promise;
    }

    void blockUser(User subject, User target) {
        mRoot.child(BANS).child(subject.getUid()).child(BANLIST).child(target.getUid()).setValue(true);
    }

    GoodStatement hasBlockedUser(User subject, User target) {
        DatabaseReference ref = mRoot.child(BANS).child(subject.getUid()).child(BANLIST).child(target.getUid());
        return DbUserHandler.getInstance().hasReference2(ref);
    }
}
