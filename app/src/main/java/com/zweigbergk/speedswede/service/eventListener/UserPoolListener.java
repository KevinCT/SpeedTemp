package com.zweigbergk.speedswede.service.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.service.DataChange;
import com.zweigbergk.speedswede.util.Client;

public class UserPoolListener implements ChildEventListener {

    private Client<DataChange<User>> mClient;

    public UserPoolListener(Client<DataChange<User>> client) {
        mClient = client;
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        User user = new UserProfile(dataSnapshot.child("displayName").getValue().toString(),
                dataSnapshot.child("uid").getValue().toString());

        Log.d("UserPoolListener", "added");

        mClient.supply(DataChange.added(user));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Log.d("UserPoolListener", "changed");

        User user = new UserProfile(dataSnapshot.child("displayName").getValue().toString(),
                dataSnapshot.child("uid").getValue().toString());
        mClient.supply(DataChange.modified(user));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        User user = new UserProfile(dataSnapshot.child("displayName").getValue().toString(),
                dataSnapshot.child("uid").getValue().toString());
        mClient.supply(DataChange.removed(user));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(Constants.ERROR, databaseError.getMessage());
        mClient.supply(DataChange.cancelled(null));
    }
}
