package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.adapter.MessageAdapter;

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


        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        View message = inflater.inflate(R.layout.fragment_message_user, null);
        //((TextView)message.findViewById(R.id.message_textview_user)).setText("Hello");
        //((LinearLayout)view.findViewById(R.id.fragment_chat_linearlayout)).addView(message);
        chatView = (ListView) view.findViewById(R.id.fragment_message_view);
        mMessageAdapter = new MessageAdapter();
        chatView.setAdapter(mMessageAdapter);

        return view;
    }
}
