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
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.Statement;
import com.zweigbergk.speedswede.util.Client;

import static com.zweigbergk.speedswede.Constants.CHATS;
import static com.zweigbergk.speedswede.Constants.POOL;
import static com.zweigbergk.speedswede.Constants.USERS;

public enum DatabaseHandler {
    INSTANCE;

    public enum DatabaseNode {
        CHATS, USERS
    }

    public static final String TAG = DatabaseHandler.class.getSimpleName().toUpperCase();

    public static final String BANS = "bans";

    private static boolean mFirebaseConnectionStatus = false;

    private static Banner mBanner = new Banner();

    private static DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public static boolean hasConnection() {
        return mFirebaseConnectionStatus;
    }

    public static DatabaseHandler getInstance() {
        return INSTANCE;
    }

    public static void onStartup() {
        DbChatHandler.INSTANCE.initialize();
        DbUserHandler.INSTANCE.initialize();
    }


    public static void registerListener(DatabaseNode node) {
        switch (node) {
            case CHATS:
                DbChatHandler.getInstance().registerChatsListener();
                break;
            case USERS:
                DbUserHandler.getInstance().registerUsersListener();
        }
    }

    public static ChatReference get(Chat chat) {
        return ChatReference.create(chat);
    }

    public static UserReference get(User user) {
        return UserReference.create(user);
    }

    public static UserListReference users() {
        return UserListReference.getInstance();
    }

    public static void setLoggedInUser(User user) {
        DbUserHandler.getInstance().setLoggedInUser(user);
    }

    public static void logout() {
        DbUserHandler.getInstance().logout();
    }

    public static PoolReference getPool() {
        return PoolReference.getInstance();
    }

    public static void bindToChatEvents(Client<DataChange<Chat>> client) {
        DbChatHandler.INSTANCE.getChatListener().addClient(client);
    }

    public static void unbindFromChatEvents(Client<DataChange<Chat>> client) {
        DbChatHandler.INSTANCE.getChatListener().removeClient(client);
    }

    private static String getFirebaseAuthUid() {
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

    public static User getActiveUser() {
        return DbUserHandler.INSTANCE.getActiveUser();
    }

    public static String getActiveUserId() {
        return DbUserHandler.INSTANCE.getActiveUserId();
    }

    public static boolean hasFirebaseConnection() {
        return mFirebaseConnectionStatus;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Statement hasUser(User user) {
        return DbUserHandler.getInstance().userExists(user);
    }

    public static Statement hasUser(String userId) {
        return DbUserHandler.getInstance().userExists(userId);
    }

    public static String generateId() {
        return root.push().getKey();
    }

    public static void sendObject(String child, Object object ){
        /*getChatWithId(chatId, chat -> {
            Banner banner = getBans(getActiveUserId());
            banner.addBan(getActiveUserId(), chat.getFirstUser().getUid(), chat.getSecondUser().getUid());
            root.child(BANS).child(getActiveUserId()).setReturnValue(banner);
            //root.child("Global"+BANS).push().setReturnValue(strangerID);
        });*/
        root.child(BANS).child(DbUserHandler.INSTANCE.getActiveUserId()).setValue(object);
    }

    public static Banner getBans(String uID){
        root.child(BANS).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public static void removeBan(String uID, String strangerID){
        Banner banner = getBans(uID);
        banner.removeBan(strangerID);

        root.child(BANS).child(uID).setValue(banner);
    }

    public static void registerConnectionHandling() {
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
