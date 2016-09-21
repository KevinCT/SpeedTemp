package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.model.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatListFragment extends Fragment {

    private TextView mChatText;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        mChatText = (TextView) view.findViewById(R.id.protein);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();


//        databaseReference.child("Fisk").setValue("Felix");
//        databaseReference.child("Fisk").setValue("Felix");

//        Log.d("key: ", databaseRef);

//        for (int i = 0; i < messages.length; i++) {
//            databaseReference.child("Mes")
//        }

//        databaseReference.child("messages").setValue(getDummyMessages());
//        databaseReference.child("test1").setValue("cookie");

//        Log.d("Current user: ", FirebaseAuth.getInstance().getCurrentUser().getUid());

//        Message m = new Message("Jonathan", "Ny text", (new Date()).getTime());
//        databaseReference.child("messages").push().setValue(m);

        databaseReference.child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildAdded", dataSnapshot.child("name").getValue().toString());
                Log.d("onChildAdded", dataSnapshot.child("text").getValue().toString());
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

//        databaseReference.child("Fisk").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d("Snap", dataSnapshot.getValue().toString());
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }


    private List<Message> getDummyMessages() {

        long timeStamp = (new Date()).getTime();

        String[] name = { "Andreas", "Felix" };

        List<Message> dummyMessages = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            dummyMessages.add(new Message(name[i % 2], "Dummy " + i + " text " + i, timeStamp += 123));
        }

        return dummyMessages;

    }
}
