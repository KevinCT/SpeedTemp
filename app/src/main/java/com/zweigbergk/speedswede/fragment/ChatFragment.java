package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.interactor.BanInteractor;
import com.zweigbergk.speedswede.util.Client;

import java.util.Date;

public class ChatFragment extends Fragment implements Client<DataChange<Message>> {
    public static final String TAG = ChatFragment.class.getSimpleName().toUpperCase();

    private RecyclerView chatRecyclerView;
    private Chat mChat;
    //TODO presenter between interactor and fragment
    private BanInteractor banInteractor;

    private EditText mInputBox;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        banInteractor = new BanInteractor();
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

                //DatabaseHandler.INSTANCE.banUser(mChat.getSecondUser().getUid());
                banInteractor.addBan(DatabaseHandler.INSTANCE.getActiveUserId(),mChat.getFirstUser().getUid(),mChat.getSecondUser().getUid());
                return true;
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

    public void setChat(Chat newChat) {
        Chat oldChat = mChat;

        //We no longer want updates from the old chat. Remove us as a client from the old chat.
        if (oldChat != null) {
            DatabaseHandler.INSTANCE.removeChatMessageClient(oldChat, this);
        }

        //We DO want updates from the new chat! Add us as a client to that one :)
        DatabaseHandler.INSTANCE.addChatMessageClient(newChat, this);

        mChat = newChat;
        getMessageAdapter().clear();
    }

    private void onButtonClick(View view) {
        String messageText = mInputBox.getText().toString();

        if (messageText.trim().length() > 0) {
            postMessage(messageText);
        }
    }

    private void postMessage(String text) {
        Message message = new Message(DatabaseHandler.INSTANCE.getActiveUserId(), text, getCurrentTime());
        DatabaseHandler.INSTANCE.postMessageToChat(mChat, message);

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
        if ((message.getId() != null && message.getId().equals(DatabaseHandler.INSTANCE.getActiveUserId()))
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