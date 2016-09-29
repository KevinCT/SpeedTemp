package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Lists;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MessageListener implements ChildEventListener {
    public static final String TAG = MessageListener.class.getSimpleName().toUpperCase();

    private Set<Client<DataChange<Message>>> mClients;

    public MessageListener(Collection<Client<DataChange<Message>>> clients) {
        mClients = new HashSet<>(clients);
    }

    public void addClient(Client<DataChange<Message>> client) {
        mClients.add(client);
    }

    public void removeClient(Client<DataChange<Message>> client) {
        mClients.remove(client);
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Message message = dataSnapshot.getValue(Message.class);
        Log.d(Constants.DEBUG, "We have a new message: " + message.getText());
        Lists.forEach(mClients, client -> client.supply(DataChange.added(message)));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Message message = dataSnapshot.getValue(Message.class);
        Lists.forEach(mClients, client -> client.supply(DataChange.modified(message)));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);
        Lists.forEach(mClients, client -> client.supply(DataChange.removed(message)));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(Constants.ERROR, databaseError.getMessage());
        Lists.forEach(mClients, client -> client.supply(DataChange.cancelled(null)));
    }

    @Override
    public boolean equals(Object other) {
        return other != null && this.getClass() == other.getClass() && hashCode() == other.hashCode();
    }
}
