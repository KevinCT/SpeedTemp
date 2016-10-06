package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DbChatHandler;
import com.zweigbergk.speedswede.util.Client;

import java.util.Collection;

public class WellBehavedChatListener extends FirebaseDataListener<Chat> implements ChildEventListener {
    public static final String TAG = WellBehavedChatListener.class.getSimpleName().toUpperCase();

    public WellBehavedChatListener() {
        super();
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        DbChatHandler.INSTANCE.createChatFrom(dataSnapshot).then(this::notifyAdded);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        DbChatHandler.INSTANCE.createChatFrom(dataSnapshot).then(this::notifyChanged);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        DbChatHandler.INSTANCE.createChatFrom(dataSnapshot).then(this::notifyRemoved);
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
    public boolean equals(Object other) {
        return other != null && this.getClass() == other.getClass() && hashCode() == other.hashCode();
    }
}
