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
import com.zweigbergk.speedswede.core.SkillCategory;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.eventListener.UserListener;
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.async.FirebasePromise;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.factory.UserFactory;


import static com.zweigbergk.speedswede.Constants.POOL;
import static com.zweigbergk.speedswede.Constants.SKILL_CATEGORY;
import static com.zweigbergk.speedswede.Constants.USERS;
import static com.zweigbergk.speedswede.Constants.BANS;
import static com.zweigbergk.speedswede.Constants.BANLIST;
import static com.zweigbergk.speedswede.util.async.PromiseNeed.SNAPSHOT;

class DbUserHandler extends DbTopLevelHandler {
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

    // NOTE: Listening to all Users all the time is not optimal.
    // It's whatever if it doesn't cause issues.
    void registerUsersListener() {
        mUsersListener = new UserListener();

        Log.d(TAG, "In registerUsersListener");
        Query userRef = mRoot.child(USERS);
        userRef.addChildEventListener(mUsersListener);
        userRef.keepSynced(true);
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

    public Promise<User> pullUser(String uid) {
//        Promise.Result<User> userResult = items -> {
//            DataSnapshot snapshot = items.getSnapshot(SNAPSHOT);
//            return new User(Lists.map(snapshot.getChildren(), DataSnapshot::getKey));
//        }


        final Promise<User> promise = Promise.create();

        if(uid != null) {
            mRoot.child(Constants.USERS).child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            UserFactory.serializeUser(promise, dataSnapshot);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            promise.setPromiseFailed(true);
                        }
                    });
        }

        return promise;
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

    void setUserSkill(User user, SkillCategory value) {
        mRoot.child(USERS).child(user.getUid()).child(SKILL_CATEGORY).setValue(value);
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

    Promise<Banner> getBans(String uid){
        Promise.Result<Banner> bannerResult = items -> {
            DataSnapshot snapshot = items.getSnapshot(SNAPSHOT);
            return new Banner(Lists.map(snapshot.getChildren(), DataSnapshot::getKey));
        };

        Promise<Banner> bannerPromise = new Promise<>(bannerResult, SNAPSHOT);

        FirebasePromise snapshotPromise = new FirebasePromise(databasePath(BANS, uid, BANLIST));
        return snapshotPromise.thenPromise(SNAPSHOT, bannerPromise);
    }

    void blockUser(User subject, User target) {
        mRoot.child(BANS).child(subject.getUid()).child(BANLIST).child(target.getUid()).setValue(true);
    }

    Statement hasBlockedUser(User subject, User target) {
        DatabaseReference ref = mRoot.child(BANS).child(subject.getUid()).child(BANLIST).child(target.getUid());
        return DbUserHandler.getInstance().hasReference(ref);
    }
}
