package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.service.DatabaseHandler;
import com.zweigbergk.speedswede.util.TestFactory;

import junit.framework.Test;

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

        view.findViewById(R.id.match_button).setOnClickListener(this::addUser);

        this.updateDebugArea((TextView) view.findViewById(R.id.fragment_chat_list_debug_area));

        return view;
    }

    private void updateDebugArea(TextView debugArea) {
        StringBuilder usersInPool = new StringBuilder();


        DatabaseHandler.INSTANCE.getMatchingPool(user -> usersInPool.append(user.getUid() + "\n"));

        debugArea.setText("Users in pool:\n" + usersInPool.toString());

    }

    public void addUser(View view) {
        ChatMatcher.INSTANCE.pushUser(TestFactory.mockUser("tester2", "tester2"));

        for (User user : ChatMatcher.INSTANCE.getPool()) {
            Log.d("User in pool: ", user.toString());
            Toast.makeText(getContext(), "User in pool: " + user.toString(), Toast.LENGTH_LONG).show();

        }

    }
}
