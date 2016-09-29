package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;

import com.zweigbergk.speedswede.adapter.ChatAdapter;
import com.zweigbergk.speedswede.adapter.ChatListAdapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;

import java.util.Arrays;

public class ChatListFragment extends Fragment {

    //ListView chatList;
    //ChatListAdapter chatListAdapter;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ChatMatcher.INSTANCE.addEventCallback(DatabaseEvent.ADDED, this::onUserAddedToChatPool);

    }

    private void onUserAddedToChatPool(User user) {
        ChatMatcher.INSTANCE.match();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        //chatList = (ListView) view.findViewById(R.id.chat_listView);
        //chatList.setAdapter();
        view.findViewById(R.id.match_button).setOnClickListener(this::addUser);


        //this.updateDebugArea((TextView) view.findViewById(R.id.fragment_chat_list_debug_area));

        return view;
    }

    private void updateDebugArea(TextView debugArea) {
//        StringBuilder usersInPool = new StringBuilder();

//        DatabaseHandler.INSTANCE.getMatchingPool(user -> usersInPool.append(user.getId() + "\n"));
//        for (User user : ChatMatcher.INSTANCE.getPool()) {
//            usersInPool.append(user.getId() + "\n");
//            debugArea.setText("Users in pool2:\n" + user.getId().toString());
////            Log.d("User in pool: ", );
//        }
//
//        debugArea.setText("Users in pool2:\n" + usersInPool.toString());
    }



    public void addUser(View view) {
        ChatMatcher.INSTANCE.pushUser(new UserProfile("Namn", DatabaseHandler.INSTANCE.getLoggedInUser().getUid()));
    }
}
