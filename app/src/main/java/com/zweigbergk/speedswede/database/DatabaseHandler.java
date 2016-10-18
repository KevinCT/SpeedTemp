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

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import com.zweigbergk.speedswede.util.collection.HashMap;

import static com.zweigbergk.speedswede.Constants.BANLIST;
import static com.zweigbergk.speedswede.Constants.BANS;

public enum DatabaseHandler {
    INSTANCE;

    public static String facebookUserID = "";

    public enum DatabaseNode {
        CHATS, USERS
    }

    public static final String TAG = DatabaseHandler.class.getSimpleName().toUpperCase();

    private static boolean mFirebaseConnectionStatus = false;

    private static DatabaseReference root = FirebaseDatabase.getInstance().getReference();

    public static boolean hasConnection() {
        return mFirebaseConnectionStatus;
    }

    public static DatabaseHandler getInstance() {
        return INSTANCE;
    }

    public static void onStartup() {
        DbChatHandler.getInstance().initialize();
        DbUserHandler.getInstance().initialize();
    }

    public static void registerListener(DatabaseNode node) {
        switch (node) {
            case USERS:
                DbUserHandler.getInstance().registerUsersListener();
                break;
            default:
                Log.w(TAG, "registerListener(): There is no setting for that node.");
        }
    }

    public static ChatReference getReference(Chat chat) {
        return ChatReference.create(chat);
    }

    public static UserReference getReference(User user) {
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
        DbChatHandler.getInstance().getChatListener().addClient(client);
    }

    public static void unbindFromChatEvents(Client<DataChange<Chat>> client) {
        DbChatHandler.getInstance().getChatListener().removeClient(client);
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
        return DbUserHandler.getInstance().getActiveUser();
    }

    public static String getActiveUserId() {
        return DbUserHandler.getInstance().getActiveUserId();
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

    public static Statement hasUsers(Chat chat) {
        return DbChatHandler.getInstance().hasUsers(chat);
    }

    public static Statement hasUser(String userId) {
        return DbUserHandler.getInstance().userExists(userId);
    }

    public static String generateId() {
        return root.push().getKey();
    }

    public static void sendObject(String child, Banner banner ){
        HashMap<String, Boolean> map = new HashMap<>();
        Lists.forEach(banner.getBanList(), uid -> map.put(uid, true));
        root.child(BANS).child(DbUserHandler.getInstance().getActiveUserId()).child(BANLIST).setValue(map);
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
