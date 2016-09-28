package com.zweigbergk.speedswede.service.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.ConversationEvent;
import com.zweigbergk.speedswede.service.DataChange;
import com.zweigbergk.speedswede.service.DatabaseHandler;
import com.zweigbergk.speedswede.util.Client;

import java.util.Date;

public class MessageListener implements ChildEventListener {

    private Client<DataChange<Message>> mClient;

    public MessageListener(Client<DataChange<Message>> client) {
        mClient = client;
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Message message = dataSnapshot.getValue(Message.class);
        Log.d(Constants.DEBUG, "We have a new message: " + message.getText());
        mClient.supply(DataChange.added(message));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Message message = dataSnapshot.getValue(Message.class);
        mClient.supply(DataChange.modified(message));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);
        mClient.supply(DataChange.removed(message));
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
