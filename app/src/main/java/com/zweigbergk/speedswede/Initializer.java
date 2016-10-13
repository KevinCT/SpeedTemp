package com.zweigbergk.speedswede;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.DatabaseHandler.DatabaseNode;
import com.zweigbergk.speedswede.util.async.Statement;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date;

import static com.zweigbergk.speedswede.Constants.USERS;

public class Initializer {

    public static final String TAG = Initializer.class.getSimpleName().toUpperCase();

    public static void onLogin() {
        DatabaseHandler.onStartup();

        addUserToDatabase();

        DatabaseHandler.registerListener(DatabaseNode.CHATS);
        DatabaseHandler.registerListener(DatabaseNode.USERS);
        DatabaseHandler.getPool().bind(ChatMatcher.INSTANCE::handleUser);

        SimpleDateFormat f = new SimpleDateFormat("EE MMM dd", Locale.getDefault());
        Log.e("DATE", f.format(new Date()));
    }

    private static void addUserToDatabase() {
        User activeUser = DatabaseHandler.getActiveUser();
        Statement containsUser = DatabaseHandler.hasUser(activeUser);

        DatabaseHandler.users().push(activeUser);

        /*containsUser.onFalse(() -> {
            Log.d(TAG, "Pushing " + activeUser.getUid());
            DatabaseHandler.users().push(activeUser);
        });
        containsUser.onTrue(() -> Log.d(TAG, "We have you...?"));*/
    }
}
