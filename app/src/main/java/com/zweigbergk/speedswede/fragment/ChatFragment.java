package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.adapter.MessageAdapter;
import com.zweigbergk.speedswede.core.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment {
    private MessageAdapter mMessageAdapter;
    private ListView chatView;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment






//        databaseReference.child("Fisk").setValue("Felix");
//        databaseReference.child("Fisk").setValue("Felix");

        Log.d("Current user: ", FirebaseAuth.getInstance().getCurrentUser().getUid());

//        Log.d("key: ", databaseRef);

//        for (int i = 0; i < messages.length; i++) {
//            databaseReference.child("Mes")
//        }

//        databaseReference.child("messages").setValue(getDummyMessages());
//        databaseReference.child("test1").setValue("cookie");

//        Log.d("Current user: ", FirebaseAuth.getInstance().getCurrentUser().getUid());

//        Message m = new Message("Jonathan", "Ny text", (new Date()).getTime());
//        databaseReference.child("messages").push().setValue(m);


//        mMessageList=new ArrayList<>();
//        Message message1 = new Message("apa", "Jag lära svenska!", 123);
//        Message message2 = new Message("apa", "S", 123);
//        Message message3 = new Message("apa", "hallå där", 123);
//        Message message4 = new Message("apa", "hallå där", 123);
//
//        mMessageList.add(message1);
//        mMessageList.add(message2);
//        mMessageList.add(message3);
//        mMessageList.add(message4);

//        mText = (TextView) mView.findViewById(R.id.message_textview_user);
//        mText.setText(mMessageList.get(i).getText());


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






        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        View message = inflater.inflate(R.layout.fragment_message_user, null);
        //((TextView)message.findViewById(R.id.message_textview_user)).setText("Hello");
        //((LinearLayout)view.findViewById(R.id.fragment_chat_linearlayout)).addView(message);
        chatView = (ListView) view.findViewById(R.id.fragment_message_view);


        mMessageAdapter = new MessageAdapter();
        chatView.setAdapter(mMessageAdapter);

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
