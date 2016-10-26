package com.zweigbergk.speedswede.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;

import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.Locale;

public class DatabaseHandler {

    private static final String TAG = DatabaseHandler.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    public static void registerUsersListener() {
        DbUserHandler.getInstance().registerUsersListener();
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

    public static User getActiveUser() {
        return DbUserHandler.getInstance().getActiveUser();
    }

    public static String getActiveUserId() {
        return DbUserHandler.getInstance().getActiveUserId();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static Statement hasUsers(Chat chat) {
        return DbChatHandler.getInstance().hasUsers(chat);
    }


    public static void registerConnectionHandling() {
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connectionStatus = snapshot.getValue(Boolean.class);
                Log.d(TAG, "Firebase connection status changed to: " + connectionStatus);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }
}
