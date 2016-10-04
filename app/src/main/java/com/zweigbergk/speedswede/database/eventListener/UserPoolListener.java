package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.firebase.DbUserHandler;

import java.util.Collections;

public class UserPoolListener extends FirebaseDataListener<User> implements ChildEventListener {

    public static final String TAG = UserPoolListener.class.getSimpleName().toUpperCase();

    public UserPoolListener() {
        super(Collections.emptySet());
    }

    private void notifyAdded(User user) {
        notifyClients(DatabaseEvent.ADDED, user);
    }

    private void notifyRemoved(User user) {
        notifyClients(DatabaseEvent.REMOVED, user);
    }

    private void notifyChanged(User user) {
        notifyClients(DatabaseEvent.CHANGED, user);
    }

    private void notifyInterrupted() {
        notifyClients(DatabaseEvent.INTERRUPED, null);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        User user = DbUserHandler.INSTANCE.convertToUser(dataSnapshot);
        Log.d(TAG, String.format("User with name %s has been added to UserPoolListener.onChildAdded()", user.getDisplayName()));
        notifyAdded(user);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        User user = DbUserHandler.INSTANCE.convertToUser(dataSnapshot);
        notifyChanged(user);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        User user = DbUserHandler.INSTANCE.convertToUser(dataSnapshot);
        notifyRemoved(user);
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
