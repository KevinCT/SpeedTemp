package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.Client;

public class ChatListener implements ChildEventListener {

    private Client<DataChange<Chat>> mClient;

    public ChatListener(Client<DataChange<Chat>> client) {
        mClient = client;
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Chat chat = DatabaseHandler.INSTANCE.convertToChat(dataSnapshot);
        mClient.supply(DataChange.added(chat));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Chat chat = DatabaseHandler.INSTANCE.convertToChat(dataSnapshot);
        mClient.supply(DataChange.modified(chat));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Chat chat = DatabaseHandler.INSTANCE.convertToChat(dataSnapshot);
        mClient.supply(DataChange.removed(chat));
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
