package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baoyz.widget.PullRefreshLayout;
import com.ogaclejapan.arclayout.ArcLayout;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.adapter.ChatAdapter;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.ChildCountListener;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.ParcelHelper;
import com.zweigbergk.speedswede.util.collection.List;

public class ChatListFragment extends Fragment implements ChildCountListener {

    private static final String TAG = ChatListFragment.class.getSimpleName().toUpperCase();
    public static final String TAG_CHATLIST = "ChatList";
    private View view;

    private RecyclerView chatListView;
    private ChatAdapter adapter;
    private ImageView backgroundImageView;

    public ChatListFragment() {
        super();

        if (getArguments() == null) {
            setArguments(new Bundle());
            Log.d(TAG, "Arguments was null. Setting arguments to new bundle.");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setHasOptionsMenu(true);

        adapter = new ChatAdapter();

        //Make us switch to the chat if we click its view.
        adapter.addEventClient(ChatAdapter.Event.CHAT_VIEW_CLICKED,
                ((ChatActivity) getActivity())::displayChat);
    }

    private void setupSwipeRefresh(View view) {
        PullRefreshLayout layout = (PullRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        layout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimaryLight),
                getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimaryLight),
                getResources().getColor(R.color.colorPrimary));

        layout.setOnRefreshListener(() -> {
            Handler handler = new Handler();
            handler.postDelayed(() -> layout.setRefreshing(false), 200);
        });
    }

    private void saveState() {
        Log.d(TAG, "saveState()");
        Bundle bundle = getArguments();
        List<Chat> list = adapter.getChats();
        Log.d(TAG, "Saving chats to arguments. Chat amount: " + list.size());
        ParcelHelper.saveParcableList(bundle, list, TAG_CHATLIST);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_chat_list, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        this.view = view;
        chatListView = (RecyclerView) view.findViewById(R.id.fragment_chat_list_view);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        chatListView.setLayoutManager(manager);
        chatListView.setAdapter(adapter);
        backgroundImageView = (ImageView) this.view.findViewById(R.id.fragment_chat_list_default_background);

        adapter.setView(this);

        view.findViewById(R.id.match_button).setOnClickListener(this::addUser);

        DatabaseHandler.bindToChatEvents(adapter::notifyChange);

        Log.d(TAG, "onCreateView");

        setupSwipeRefresh(view);

        loadSavedState();

        return view;
    }

    public void onUpdate() {
        Log.d(TAG, "Update background in ChatListFragment");

        if (adapter.getItemCount() == 0 ) {
            backgroundImageView.setImageResource(R.drawable.default_background_v1);
        } else {
            backgroundImageView.setImageResource(0);
        }
    }

    @Override
    public void onDestroyView() {
        DatabaseHandler.unbindFromChatEvents(adapter::notifyChange);
        Log.d(TAG, "onDestroyView");

        super.onDestroyView();
    }

    private void loadSavedState() {
        Log.d(TAG, "loadSavedState()");
        Bundle savedState = getArguments();

        if (savedState != null) {
            Log.d(TAG, "We have a saved state!");
            List<Chat> savedChats = ParcelHelper.retrieveParcableList(savedState, TAG_CHATLIST);
            Log.d(TAG, "Saved chat amount: " + savedChats);
                savedChats.foreach(chat -> adapter.notifyChange(DataChange.added(chat)));

            savedState.clear();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState()");
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
        getActivity().invalidateOptionsMenu();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

        saveState();
    }

    public void addUser(View view) {
        ChatMatcher.INSTANCE.pushUser(DatabaseHandler.getActiveUser());
    }

    public void startSettings() {
        ((ChatActivity) getActivity()).startSettings();
    }
}
