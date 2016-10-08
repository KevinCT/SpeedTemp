package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.adapter.ChatAdapter;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.database.DatabaseHandler;

public class ChatListFragment extends Fragment {

    public static final String TAG = ChatListFragment.class.getSimpleName().toUpperCase();

    RecyclerView chatListView;
    ChatAdapter mAdapter;

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

        chatListView = (RecyclerView) view.findViewById(R.id.fragment_chat_list_view);

        mAdapter = new ChatAdapter();

        //Make us switch to the chat if we click its view.
        mAdapter.addEventClient(ChatAdapter.Event.CHAT_VIEW_CLICKED,
                ((ChatActivity) getActivity())::displayChat);

        chatListView.setLayoutManager(new LinearLayoutManager(getContext()));
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
