package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;

public class ChatFragment extends Fragment {

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
        ((TextView)message.findViewById(R.id.message_textview_user)).setText("Hello");
        ((LinearLayout)view.findViewById(R.id.fragment_chat_linearlayout)).addView(message);
        return view;
    }
}
