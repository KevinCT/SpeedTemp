package com.zweigbergk.speedswede.service;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.util.Client;

import com.zweigbergk.speedswede.core.local.*;
import com.zweigbergk.speedswede.util.TestFactory;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum DatabaseHandler {
    INSTANCE;

    DatabaseHandler() { }

    public static final String CONVERSATION = "conversation";
    public static final String CHATS = "chats";
    public static final String POOL = "pool";
    public static final String USER_NAME = "displayName";
    public static final String UID = "uid";

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference fetchChatConversationByUid(String chatUid) {
        return mDatabaseReference.child(CHATS).child(chatUid).child(CONVERSATION);
    }

    public void getMatchingPool(Client<User> client) {
        List<String> userStrings = new LinkedList<>();

        mDatabaseReference.child(POOL).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = new UserProfile((String) dataSnapshot.child(USER_NAME).getValue(), (String) dataSnapshot.child(UID).getValue());
                client.supply(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setMatchingPool() {
        List<User> userList = ChatMatcher.INSTANCE.getPool();
        mDatabaseReference.child(POOL).setValue(userList);
    }

    public User getLoggedInUser() {
        return new User() {
            @Override
            public String getUid() {
                return FirebaseAuth.getInstance().getCurrentUser().getUid();
            }

            @Override
            public boolean isAnonymous() {
                return FirebaseAuth.getInstance().getCurrentUser().isAnonymous();
            }

            @Override
            public String getDisplayName() {
                return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            }

            @Override
            public Uri getPhotoUrl() {
                return FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
            }

            @Override
            public String getEmail() {
                return FirebaseAuth.getInstance().getCurrentUser().getEmail();
            }
        };
    }

    public void registerConversationListener(String chatUid, Client<DataChange<Message>> client) {
        DatabaseReference conversationReference = fetchChatConversationByUid(chatUid);
        conversationReference.keepSynced(true);

        conversationReference.addChildEventListener(new ChildEventListener() {
            // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
            // Thus there is no need for an initial SingleValueEventListener.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                Log.d(Constants.DEBUG, "We have a new message: " + message.getText());
                client.supply(DataChange.added(message));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                client.supply(DataChange.modified(message));
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Message message = dataSnapshot.getValue(Message.class);
                client.supply(DataChange.removed(message));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.ERROR, databaseError.getMessage());
                client.supply(DataChange.cancelled(null));
            }
        });
    }

    public void postMessageToChat(String chatId, Message message) {
        mDatabaseReference.child(CHATS).child(chatId).child(CONVERSATION).push().setValue(message);
    }

    public static class DataChange<ObjectType> {

        private final ConversationEvent mEvent;
        private final ObjectType mData;

        DataChange(ObjectType data, ConversationEvent event) {
            mData = data;
            mEvent = event;
        }

        public ObjectType getItem() {
            return mData;
        }

        public ConversationEvent getEvent() {
            return mEvent;
        }

        static <ObjectType> DataChange<ObjectType> added(ObjectType data) {
            return new DataChange<>(data, ConversationEvent.MESSAGE_ADDED);
        }

        static <ObjectType> DataChange<ObjectType> modified(ObjectType data) {
            return new DataChange<>(data, ConversationEvent.MESSAGE_MODIFIED);
        }

        static <ObjectType> DataChange<ObjectType> removed(ObjectType data) {
            return new DataChange<>(data, ConversationEvent.MESSAGE_REMOVED);
        }

        static <ObjectType> DataChange<ObjectType> cancelled(ObjectType data) {
            return new DataChange<>(data, ConversationEvent.INTERRUPED);
        }


    }

}
