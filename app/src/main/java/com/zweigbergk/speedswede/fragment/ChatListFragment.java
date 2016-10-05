package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.adapter.ChatListAdapter;

import android.support.v4.app.Fragment;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.firebase.DbChatHandler;
import com.zweigbergk.speedswede.database.firebase.DbUserHandler;

import java.util.ArrayList;

import static android.R.attr.id;

public class ChatListFragment extends Fragment {

    public static final String TAG = ChatListFragment.class.getSimpleName().toUpperCase();

    ListView chatList;
    ChatListAdapter mChatlistAdapter;


    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ChatMatcher.INSTANCE.addPoolClient(DatabaseEvent.ADDED, this::onUserAddedToChatPool);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_chat_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_chat_list_settings_button:
                Log.d("DEBUG", "pressing settings button??");
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void onUserAddedToChatPool(User user) {
        Log.d(TAG, " onUserAddedToChatPool " + user.getUid());
        ChatActivity activity = (ChatActivity) getActivity();
        ChatMatcher.INSTANCE.match(activity::setChatForChatFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        chatList = (ListView) view.findViewById(R.id.chat_listView);

        mChatlistAdapter = new ChatListAdapter(new ArrayList<>()); //Chats here
        chatList.setAdapter(mChatlistAdapter);

        view.findViewById(R.id.match_button).setOnClickListener(this::addUser);


        //this.updateDebugArea((TextView) view.findViewById(R.id.fragment_chat_list_debug_area));

        return view;
    }

    public void addUser(View view) {
        ChatMatcher.INSTANCE.pushUser(DbUserHandler.INSTANCE.getLoggedInUser());
        Log.d(TAG, "WHATEVER");
    }
}
