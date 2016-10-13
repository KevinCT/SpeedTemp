package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.util.async.GoodStatement;

public abstract class DbHandler {
    public static final String TAG = DbHandler.class.getSimpleName().toUpperCase();


    void delete(DatabaseReference ref) {
        ref.removeValue();
    }

    GoodStatement hasReference(DatabaseReference ref) {
        GoodStatement statement = new GoodStatement();

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


}
