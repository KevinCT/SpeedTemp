package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

        mAdapter = new ChatAdapter();

        //Make us switch to the chat if we click its view.
        mAdapter.addEventClient(ChatAdapter.Event.CHAT_VIEW_CLICKED,
                ((ChatActivity) getActivity())::displayChat);

        DatabaseHandler.bindToChatEvents(mAdapter::notifyChange);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_chat_list,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        chatListView = (RecyclerView) view.findViewById(R.id.fragment_chat_list_view);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        chatListView.setLayoutManager(manager);
        chatListView.setAdapter(mAdapter);

        view.findViewById(R.id.match_button).setOnClickListener(this::addUser);

        return view;
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
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void addUser(View view) {
        ChatMatcher.INSTANCE.pushUser(DatabaseHandler.getActiveUser());
    }

    public void startSettings() {
        ((ChatActivity) getActivity()).startSettings();
    }
}
