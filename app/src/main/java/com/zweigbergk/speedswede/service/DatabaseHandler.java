package com.zweigbergk.speedswede.service;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Executable;

import java.util.ArrayList;
import java.util.List;

public enum DatabaseHandler {
    INSTANCE;

    private DatabaseReference mDatabaseReference;

    public void fetchInitialData(Client<List<Message>> client) {
        List<Message> messageList = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        mDatabaseReference.child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d("dataSnapshot: ", snapshot.child("name").getValue().toString());
                    Message message = new Message(snapshot.child("name").getValue().toString(), snapshot.child("text").getValue().toString());
                    messageList.add(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        Log.d("DEBUG", "Supplying with: " + messageList.size());
        client.supply(messageList);
    }

    public void registerConversationListener() {

        // todo: Fetch only new message / Andreas   

        //        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        databaseReference.child("messages").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Message message = new Message(dataSnapshot.child("name").getValue().toString(), dataSnapshot.child("text").getValue().toString());
//                mMessageList.add(message);
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

    }

}
