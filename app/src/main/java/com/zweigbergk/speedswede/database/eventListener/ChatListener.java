package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.List;

public class ChatListener implements ChildEventListener {

    private Client<DataChange<Chat>> mClient;

    public ChatListener(Client<DataChange<Chat>> client) {
        mClient = client;
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.

    private static Chat convertToChat(DataSnapshot snapshot) {
        //TODO: is key also its id????
        String id = snapshot.getKey();
        long timeStamp = (long) snapshot.child("timeStamp").getValue();
        User firstUser = new UserProfile("user1", "user1");
        User secondUser = new UserProfile("user2", "user2");
        DataSnapshot messagesSnapshot = snapshot.child("conversation");

        List<Message> messages = new ArrayList<>();
        for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
            Message message = messageSnapshot.getValue(Message.class);
            messages.add(message);
        }
        Chat chat = new Chat(id, timeStamp, messages, firstUser, secondUser);
        return chat;
    }
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Chat chat = convertToChat(dataSnapshot);
        //Chat chat = dataSnapshot.getValue(Chat.class);

        mClient.supply(DataChange.added(chat));
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        Chat chat = dataSnapshot.getValue(Chat.class);

        mClient.supply(DataChange.modified(chat));
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Chat chat = dataSnapshot.getValue(Chat.class);

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
