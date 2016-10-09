package com.zweigbergk.speedswede.presenter;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.adapter.MessageAdapter;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.interactor.BanInteractor;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Time;
import com.zweigbergk.speedswede.view.ChatFragmentView;

public class ChatFragmentPresenter {
    public static final String TAG = ChatFragmentPresenter.class.getSimpleName().toUpperCase();

    private BanInteractor mBanInteractor;

    private ChatFragmentView mView;

    private Chat mChat;

    public ChatFragmentPresenter(){
        mBanInteractor = new BanInteractor();

        initializeRecyclerView();
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = mView.getRecyclerView();

        LinearLayoutManager layoutManager = mView.contextualize(LinearLayoutManager::new);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);

        MessageAdapter adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);

        adapter.addEventCallback(DatabaseEvent.ADDED, this::smoothScrollToBottomOfList);
    }

    private void smoothScrollToBottomOfList(Message message) {
        RecyclerView recyclerView = mView.getRecyclerView();
        int scrollOffset = recyclerView.computeVerticalScrollOffset();
        int scrollHeight = recyclerView.computeVerticalScrollRange() - recyclerView.computeVerticalScrollExtent();

        if (recyclerView.getAdapter().getItemCount() <= 0) {
            return;
        }

        // Only scroll to the bottom if the new message was posted by us,
        //   OR if you are at the relative bottom of the chat.
        if ((message.getId() != null && message.getId().equals(DatabaseHandler.getActiveUserId()))
                || (scrollHeight - scrollOffset < recyclerView.getHeight())) {
            recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
        }
    }

    public void onChatChanged(Chat newChat) {
        Chat oldChat = mChat;

        if (oldChat != null && oldChat.equals(newChat)) {
            return;
        }

        getMessageAdapter().clear();

        //We no longer want updates from the old chat. Remove us as a client from the old chat.
        if (oldChat != null) {
            DatabaseHandler.get(oldChat).unbindMessageClient(handleChatEvent);
        }

        //We DO want updates from the new chat! Add us as a client to that one :)
        DatabaseHandler.get(newChat).bindMessageClient(handleChatEvent);

        mChat = newChat;
    }

    public void onClickSend() {
        String messageText = mView.getInputText();

        if (messageText.trim().length() > 0) {
            postMessage(messageText);
        }
    }

    public void terminateChat() {
        User activeUser = DatabaseHandler.getActiveUser();
        DatabaseHandler.get(mChat).removeUser(activeUser);
    }

    private void postMessage(String messageText) {
        Message message = new Message(
                DatabaseHandler.getActiveUserId(),
                messageText,
                Time.getCurrentTime());

        DatabaseHandler.get(mChat).sendMessage(message);

        addToAdapter(message);
        mView.clearInputField();
    }

    private void addToAdapter(Message message) {
        MessageAdapter adapter = getMessageAdapter();
        adapter.onListChanged(DataChange.added(message));
    }

    public void onBanClicked(){
        String firstUserId = mChat.getFirstUser().getUid();
        String secondUserId = mChat.getSecondUser().getUid();
        mBanInteractor.addBan(firstUserId, secondUserId);
    }

    public void onChangeLanguageClicked() {
        mView.useActivity(ChatActivity::showLanguageFragment);
    }

    private MessageAdapter getMessageAdapter() {
        return (MessageAdapter) mView.getRecyclerView().getAdapter();
    }

    private final Client<DataChange<Message>> handleChatEvent = dataChange -> {
        Log.d(TAG, String.format("Calling onListChanged with change: [%s] and messageText: [%s]",
                dataChange.getEvent().toString(), dataChange.getItem().getText()));

        ((MessageAdapter) mView.getRecyclerView().getAdapter()).onListChanged(dataChange);
    };
}
