package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.util.async.Statement;

import java.util.Arrays;

public abstract class DbHandler {
    public static final String TAG = DbHandler.class.getSimpleName().toUpperCase();


    void delete(DatabaseReference ref) {
        ref.removeValue();
    }

    Statement hasReference(DatabaseReference ref) {
        Statement statement = new Statement();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "We set return value to " + dataSnapshot.exists());
                statement.setReturnValue(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "hasReference failed!");
                statement.setPromiseFailed(true);
            }
        });

        return statement;
    }

    Statement hasReferenceDebug(DatabaseReference ref) {
        Statement statement = new Statement();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "hasReferenceDebug: user exists: " + dataSnapshot.exists());
                statement.setReturnValue(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "hasReferenceDebug failed!");
                statement.setPromiseFailed(true);
            }
        });

        return statement;
    }

    DatabaseReference databasePath(String... strings) {
        return databasePath(FirebaseDatabase.getInstance().getReference(), strings);
    }

    private DatabaseReference databasePath(DatabaseReference ref, String[] strings) {
        if (strings.length > 0) {
            ref = ref.child(strings[0]);
            strings = Arrays.copyOfRange(strings, 1, strings.length);
            return databasePath(ref, strings);
        } else {
            return ref;
        }
    }


}
