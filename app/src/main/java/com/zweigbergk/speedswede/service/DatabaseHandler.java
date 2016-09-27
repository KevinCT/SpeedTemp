package com.zweigbergk.speedswede.service;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.List;

public enum DatabaseHandler {
    INSTANCE;

    public static final String CONVERSATION = "conversation";
    public static final String CHATS = "chats";

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference fetchChatConversationByUid(String chatUid) {
        return mDatabaseReference.child(CHATS).child(chatUid).child(CONVERSATION);
    }

    // TODO Create actual implementation
    public User getLoggedInUser() {
        return new User() {
            @Override
            public String getUid() {
                return null;
            }

            @Override
            public boolean isAnonymous() {
                return false;
            }

            @Override
            public String getDisplayName() {
                return Constants.TEST_USER_NAME;
            }

            @Override
            public Uri getPhotoUrl() {
                return null;
            }

            @Override
            public String getEmail() {
                return null;
            }
        };
    }

    public void registerConversationListener(String chatUid, Client<DataChange<Message>> client) {
        DatabaseReference conversationReference = fetchChatConversationByUid(chatUid);

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

    public void postMessageToChat(String chatUid, Message message) {
        mDatabaseReference.child(CHATS).child(chatUid).child(CONVERSATION).push().setValue(message);
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
