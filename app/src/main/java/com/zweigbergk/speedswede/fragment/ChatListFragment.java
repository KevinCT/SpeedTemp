package com.zweigbergk.speedswede.fragment;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.adapter.ChatListAdapter;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.database.DatabaseHandler;

public class ChatListFragment extends Fragment {

    public static final String TAG = ChatListFragment.class.getSimpleName().toUpperCase();

    ListView chatListView;
    ChatListAdapter mAdapter;

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

        chatListView = (ListView) view.findViewById(R.id.chat_listView);

        mAdapter = new ChatListAdapter();
        chatListView.setAdapter(mAdapter);

        DatabaseHandler.bindToChatEvents(mAdapter::notifyChange);

        view.findViewById(R.id.match_button).setOnClickListener(this::addUser);

        //this.updateDebugArea((TextView) view.findViewById(R.id.fragment_chat_list_debug_area));
//        ((Button) view.findViewById(R.id.addDummyMessage)).setText("Random: "+ (Math.random() * 1000));

        return view;
    }

    public void addUser(View view) {
        ChatMatcher.INSTANCE.pushUser(DatabaseHandler.getActiveUser());
    }

    public void startSettings() {
        ((ChatActivity) getActivity()).startSettings();
    }
}
