package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;

import java.util.Collections;

public class UserPoolListener extends FirebaseDataListener<User> implements ChildEventListener {

    public static final String TAG = UserPoolListener.class.getSimpleName().toUpperCase();

    public UserPoolListener() {
        super(Collections.emptySet());

        Log.d(TAG, "In constructor");
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        String userId = dataSnapshot.getKey();
        DatabaseHandler.users().pull(userId).then(this::notifyAdded);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        String userId = dataSnapshot.getKey();
        DatabaseHandler.users().pull(userId).then(this::notifyChanged);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        String userId = dataSnapshot.getKey();
        DatabaseHandler.users().pull(userId).then(this::notifyRemoved);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(Constants.ERROR, databaseError.getMessage());
        notifyInterrupted();
    }
}
