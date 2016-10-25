package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.collection.CollectionExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.Locale;

public class MessageListener extends FirebaseDataListener<Message> implements ChildEventListener {
    private static final String TAG = MessageListener.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    private String mIdentifier = "";

    public MessageListener(CollectionExtension<Client<DataChange<Message>>> clients) {
        super(clients);
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Message message = dataSnapshot.getValue(Message.class);
        notifyAdded(message);
        Log.d(TAG, Stringify.curlyFormat("onChildAdded in MessageListener, message text: {text}",
                message.getText()));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Message message = dataSnapshot.getValue(Message.class);
        notifyChanged(message);
        Log.d(TAG, Stringify.curlyFormat("onChildChanged in MessageListener, message text: {text}",
                message.getText()));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Message message = dataSnapshot.getValue(Message.class);
        notifyRemoved(message);

        Log.d(TAG, Stringify.curlyFormat("onChildRemoved in MessageListener, message text: {text}",
                message.getText()));
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(Constants.ERROR, databaseError.getMessage());
        notifyClients(DatabaseEvent.INTERRUPTED, null);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || this.getClass() != object.getClass())
            return false;

        MessageListener other = (MessageListener) object;
        Log.d(TAG, String.format("IDENTIFIERS: %s ::: %s", mIdentifier, other.mIdentifier));
        return this.mIdentifier.equals(other.mIdentifier);
    }

    @Override
    public int hashCode() {
        return this.mIdentifier.hashCode();
    }
}
