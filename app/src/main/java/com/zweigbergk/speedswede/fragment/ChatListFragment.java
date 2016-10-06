package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.adapter.ChatListAdapter;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.database.DbChatHandler;
import com.zweigbergk.speedswede.database.DbUserHandler;

public class ChatListFragment extends Fragment {

    public static final String TAG = ChatListFragment.class.getSimpleName().toUpperCase();

    ListView chatList;
    ChatListAdapter mChatlistAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
                startSettings();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        chatList = (ListView) view.findViewById(R.id.chat_listView);

        mChatlistAdapter = new ChatListAdapter();
        chatList.setAdapter(mChatlistAdapter);

        //Reads every chat user is active in from the database and puts them in our adapter
        //NOTE: This is not needed right now. However, if ChatListFragment is not the get fragment
        // in ChatActivity in the future, this WILL be necessary. Do not remove.
        //DbChatHandler.INSTANCE.getChatsByActiveUser(mChatlistAdapter::addChats);
        // TODO uncomment V
        /*DbChatHandler.INSTANCE.getChatsByActiveUser(list -> {
            Log.d(TAG, "Caught the chats from getChatsByActiveUser! Amount: " + list.size());
            mChatlistAdapter.addChats(list);
        });*/

        //Adds us as clients to any changes in the user's chat on the database. If a chat of our user
        // is added/removed/changed, our onListChanged will be notified.
        DbChatHandler.INSTANCE.addUserToChatClient(mChatlistAdapter::onListChanged);

        view.findViewById(R.id.match_button).setOnClickListener(this::addUser);

        //this.updateDebugArea((TextView) view.findViewById(R.id.fragment_chat_list_debug_area));
//        ((Button) view.findViewById(R.id.addDummyMessage)).setText("Random: "+ (Math.random() * 1000));

        return view;
    }

    public void addUser(View view) {
        ChatMatcher.INSTANCE.pushUser(DbUserHandler.INSTANCE.getLoggedInUser());
    }

    public void startSettings() {
        ((ChatActivity) getActivity()).startSettings();
    }
}
