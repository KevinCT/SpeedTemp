package com.zweigbergk.speedswede.service;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.List;

public enum DatabaseHandler {
    INSTANCE;

    public static final String CONVERSATION = "conversation";
    public static final String CHATS = "chats";

    public enum Event {ADDED, MODIFIED, REMOVED, CANCELLED }

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference fetchChatConversationByUid(String chatUid) {
        return mDatabaseReference.child(CHATS).child(chatUid).child(CONVERSATION);
    }

    public void fetchConversation(String chatUid, Client<List<Message>> client) {
        List<Message> conversation = new ArrayList<>();
        DatabaseReference conversationReference = fetchChatConversationByUid(chatUid);

        conversationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("dataSnapshot: ", snapshot.child("name").getValue().toString());
                    Message message = snapshot.getValue(Message.class);
                    conversation.add(message);
                }

                client.supply(conversation);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.ERROR, databaseError.getMessage());
            }
        });
    }

    public void registerConversationListener(String chatUid, Client<DataChange<Message>> client) {
        DatabaseReference conversationReference = fetchChatConversationByUid(chatUid);

        conversationReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
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

        private final Event mEvent;
        private final ObjectType mData;

        DataChange(ObjectType data, Event event) {
            mData = data;
            mEvent = event;
        }

        public ObjectType getItem() {
            return mData;
        }

        public Event getEvent() {
            return mEvent;
        }

        static <ObjectType> DataChange<ObjectType> added(ObjectType data) {
            return new DataChange<>(data, Event.ADDED);
        }

        static <ObjectType> DataChange<ObjectType> modified(ObjectType data) {
            return new DataChange<>(data, Event.MODIFIED);
        }

        static <ObjectType> DataChange<ObjectType> removed(ObjectType data) {
            return new DataChange<>(data, Event.REMOVED);
        }

        static <ObjectType> DataChange<ObjectType> cancelled(ObjectType data) {
            return new DataChange<>(data, Event.CANCELLED);
        }


    }

}
