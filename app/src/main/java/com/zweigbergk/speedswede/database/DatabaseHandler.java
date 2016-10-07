package com.zweigbergk.speedswede.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.Client;

public enum DatabaseHandler {
    INSTANCE;

    public static final String TAG = DatabaseHandler.class.getSimpleName().toUpperCase();

    public static final String BANS = "bans";

    private boolean mFirebaseConnectionStatus = false;

    private Banner mBanner = new Banner();

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    public boolean hasConnection() {
        return mFirebaseConnectionStatus;
    }

    public static DatabaseHandler getInstance() {
        return INSTANCE;
    }

    public void onStartup() {
        DbChatHandler.INSTANCE.initialize();
        DbUserHandler.INSTANCE.initialize();
    }


    public void registerListener(DatabaseNode node) {
        switch (node) {
            case CHATS:
                DbChatHandler.INSTANCE.registerChatsListener();
        }
    }

    public static ChatManipulator manipulate(Chat chat) {
        return ChatManipulator.create(chat);
    }

    public static UsersManipulator users() {
        return UsersManipulator.getInstance();
    }

    public void setLoggedInUser(User user) {
        DbUserHandler.getInstance().setLoggedInUser(user);
    }

    public void logout() {
        DbUserHandler.getInstance().logout();
    }

    public User convertToUser(DataSnapshot snapshot) {
        return DbUserHandler.getInstance().convertToUser(snapshot);
    }

    public static PoolManipulator manipulatePool() {
        return PoolManipulator.getInstance();
    }

    public void bindToChatEvents(Client<DataChange<Chat>> client) {
        DbChatHandler.INSTANCE.getChatListener().addClient(client);
    }

    public ChatExistanceCheck getChatById(String chatId) {
        return ChatExistanceCheck.ifExists(chatId);
    }

    public void unbindFromChatEvents(Client<DataChange<Chat>> client) {
        DbChatHandler.INSTANCE.getChatListener().removeClient(client);
    }

    public void pushTestUser() {
        DbUserHandler.getInstance().pushTestUser();
    }

    public UserPoolListener getPool() {
        return DbUserHandler.getInstance().getPoolListener();
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

    public User getActiveUser() {
        return DbUserHandler.INSTANCE.getActiveUser();
    }

    public String getActiveUserId() {
        return DbUserHandler.INSTANCE.getActiveUserId();
    }

    public boolean hasFirebaseConnection() {
        return mFirebaseConnectionStatus;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String generateId() {
        return mDatabaseReference.push().getKey();
    }

    public void sendObject(String child, Object object ){
        /*getChatWithId(chatId, chat -> {
            Banner banner = getBans(getActiveUserId());
            banner.addBan(getActiveUserId(), chat.getFirstUser().getUid(), chat.getSecondUser().getUid());
            mDatabaseReference.child(BANS).child(getActiveUserId()).setValue(banner);
            //mDatabaseReference.child("Global"+BANS).push().setValue(strangerID);
        });*/
        mDatabaseReference.child(BANS).child(DbUserHandler.INSTANCE.getActiveUserId()).setValue(object);
    }

    public Banner getBans(String uID){
        mDatabaseReference.child(BANS).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBanner = dataSnapshot.getValue(Banner.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBanner = new Banner();

            }

        });
        return mBanner;

    }

    public void removeBan(String uID, String strangerID){
        Banner banner = getBans(uID);
        banner.removeBan(strangerID);

        mDatabaseReference.child(BANS).child(uID).setValue(banner);
    }

    public void registerConnectionHandling() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connectionStatus = snapshot.getValue(Boolean.class);
                Log.d(TAG, "Firebase connection status changed to: " + connectionStatus);
                mFirebaseConnectionStatus = connectionStatus;
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }
}
