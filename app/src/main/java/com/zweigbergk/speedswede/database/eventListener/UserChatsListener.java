package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.firebase.DbChatHandler;

import java.util.Collections;

public class UserChatsListener extends FirebaseDataListener<Chat> implements ChildEventListener {

    public static final String TAG = UserChatsListener.class.getSimpleName().toUpperCase();

    public UserChatsListener() {
        super(Collections.emptySet());

        Log.d(TAG, "In constructor");
    }

    private void notifyAdded(Chat chat) {
        notifyClients(DatabaseEvent.ADDED, chat);
    }

    private void notifyRemoved(Chat chat) {
        notifyClients(DatabaseEvent.REMOVED, chat);
    }

    private void notifyChanged(Chat chat) {
        notifyClients(DatabaseEvent.CHANGED, chat);
    }

    private void notifyInterrupted() {
        notifyClients(DatabaseEvent.INTERRUPTED, null);
    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        DbChatHandler.INSTANCE.convertToChat(dataSnapshot, this::notifyAdded);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        DbChatHandler.INSTANCE.convertToChat(dataSnapshot, this::notifyChanged);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        DbChatHandler.INSTANCE.convertToChat(dataSnapshot, this::notifyRemoved);
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
