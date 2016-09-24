package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.zweigbergk.speedswede.adapter.NewMessageAdapter;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.DatabaseHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment {
    private MessageAdapter mMessageAdapter;
    private RecyclerView chatView;

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

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        View message = inflater.inflate(R.layout.fragment_message_user, null);

        chatView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);
        chatView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatView.setAdapter(new NewMessageAdapter(null));

        return view;
    }
}