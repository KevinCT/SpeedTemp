package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.adapter.MessageAdapter;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.interactor.BanInteractor;
import com.zweigbergk.speedswede.presenter.ChatFragmentPresenter;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Lists;

import java.util.Collections;
import java.util.Date;

public class ChatFragment extends Fragment implements Client<DataChange<Message>> {
    public static final String TAG = ChatFragment.class.getSimpleName().toUpperCase();

    private RecyclerView chatRecyclerView;
    private Chat mChat;
    //TODO presenter between interactor and fragment
    private ChatFragmentPresenter mPresenter;

    private EditText mInputBox;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPresenter = new ChatFragmentPresenter();
        chatListAdapter = new ChatListAdapter();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_chat,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.blockUser:
                mPresenter.onBanClicked(mChat.getFirstUser().getUid(),mChat.getSecondUser().getUid());
                return true;
            case R.id.changeLangauge:
                FragmentManager manager = getFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                transaction.add(android.R.id.content, new ChangeLanguageFragment());
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            case R.id.exitChat:
                terminateChat();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        view.findViewById(R.id.fragment_chat_post_message).setOnClickListener(this::onButtonClick);

        initializeRecyclerView(view);

        mInputBox = (EditText) view.findViewById(R.id.fragment_chat_message_text);

        return view;
    }

    public void terminateChat() {
        User activeUser = DatabaseHandler.getActiveUser();

        DatabaseHandler.get(mChat).removeUser(activeUser);
    }

    public void setChat(Chat newChat) {
        Chat oldChat = mChat;

        /*if (oldChat != null && oldChat.equals(newChat)) {
            return;
        }*/

        //We no longer want updates from the old chat. Remove us as a client from the old chat.
        if (oldChat != null) {
            DatabaseHandler.get(oldChat).unbindMessageClient(this);
        }

        //We DO want updates from the new chat! Add us as a client to that one :)
        DatabaseHandler.get(newChat).bindMessageClient(this);

        mChat = newChat;
    }

    private void onButtonClick(View view) {
        String messageText = mInputBox.getText().toString();

        if (messageText.trim().length() > 0) {
            postMessage(messageText);
        }
    }

    private void postMessage(String text) {
        Message message = new Message(DatabaseHandler.getActiveUserId(), text, getCurrentTime());
        DatabaseHandler.get(mChat).sendMessage(message);

        MessageAdapter adapter = getMessageAdapter();
        adapter.onListChanged(DataChange.added(message));

        clearInputField();
    }

    private MessageAdapter getMessageAdapter() {
        return (MessageAdapter) chatRecyclerView.getAdapter();
    }

    private void clearInputField() {
        mInputBox.setText("");
    }

    private long getCurrentTime() {
        return new Date().getTime();
    }

    private void initializeRecyclerView(View view) {
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        chatRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);

        MessageAdapter adapter = new MessageAdapter();
        chatRecyclerView.setAdapter(adapter);

        adapter.addEventCallback(DatabaseEvent.ADDED, this::smoothScrollToBottomOfList);
    }

    private void smoothScrollToBottomOfList(Message message) {
        int scrollOffset = chatRecyclerView.computeVerticalScrollOffset();
        int scrollHeight = chatRecyclerView.computeVerticalScrollRange() - chatRecyclerView.computeVerticalScrollExtent();

        if (chatRecyclerView.getAdapter().getItemCount() <= 0) {
            return;
        }

        // Only scroll to the bottom if the new message was posted by us,
        //   OR if you are at the relative bottom of the chat.
        if ((message.getId() != null && message.getId().equals(DatabaseHandler.getActiveUserId()))
                || (scrollHeight - scrollOffset < chatRecyclerView.getHeight())) {
            chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount() - 1);
        }

    }

    @Override
    public void supply(DataChange<Message> dataChange) {
        Log.d(TAG, String.format("Calling onListChanged with change: [%s] and messageText: [%s]",
                dataChange.getEvent().toString(), dataChange.getItem().getText()));

        ((MessageAdapter)chatRecyclerView.getAdapter()).onListChanged(dataChange);
    }
}