package com.zweigbergk.speedswede.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.zweigbergk.speedswede.adapter.MessageAdapter;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.LocalStorage;
import com.zweigbergk.speedswede.database.UserReference;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.Time;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import java.util.Locale;

import static com.zweigbergk.speedswede.Constants.CHAT_PARCEL;


public class ChatFragmentPresenter {
    public static final String TAG = ChatFragmentPresenter.class.getSimpleName().toUpperCase();

    private ChatFragmentView mView;
    private Client<DataChange<Message>> chatEventHandler;

    private Chat mChat;

    public ChatFragmentPresenter(ChatFragmentView view){
        mView = view;
    }
    public Chat getChat(){
        return mChat;
    }

    public void setChat(Chat chat) {
        Log.d(TAG, "setChat()");

        mChat = chat;
    }

    /**
     * Tells the presenter to update the state of the view
     */
    public void invalidate() {
        Log.d(TAG, "Invalidate()");
        initializeRecyclerView();

        getMessageAdapter().clear();

        //We want updates from the new chat! Add us as a client to that one :)
        MessageAdapter adapter = (MessageAdapter) mView.getRecyclerView().getAdapter();
        chatEventHandler = createChatEventHandler(adapter);
        Log.d(TAG, "Creating new chatEventHandler, toString(): " + chatEventHandler);
        DatabaseHandler.get(mChat).bindMessages(chatEventHandler);
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = mView.getRecyclerView();

        LinearLayoutManager layoutManager = mView.contextualize(LinearLayoutManager::new);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);

        //Get locale from view
        Locale currentLocale = mView.contextualize(LanguageChanger::getCurrentLocale);

        MessageAdapter adapter = new MessageAdapter(currentLocale);
        recyclerView.setAdapter(adapter);

        recyclerView.setOnClickListener(v -> Log.d(TAG, "Clicked :D"));

        adapter.addEventCallback(DatabaseEvent.ADDED, this::smoothScrollToBottomOfList);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CHAT_PARCEL, mChat);
    }

    public void onClickSend() {
        Log.d(TAG, "onClickSend()");
        String messageText = mView.getInputText();

        if (messageText.trim().length() > 0) {
            postMessage(messageText);
        }
    }

    public void terminateChat() {
        User activeUser = DatabaseHandler.getActiveUser();
        DatabaseHandler.get(mChat).removeUser(activeUser);
        DatabaseHandler.hasUsers(mChat).then(
                result -> {
                    if(!result) {
                        DatabaseHandler.get(mChat).remove();
                    }
                }
        );
    }

    public void removeLocalChatSettings(Context context){
        LocalStorage.INSTANCE.removeSetting(context, mChat.getId());

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
        User activeUser = DatabaseHandler.getActiveUser();
        UserReference userRef = DatabaseHandler.get(activeUser);

        User stranger = mChat.getFirstUser().equals(activeUser) ?
                mChat.getSecondUser() : mChat.getFirstUser();

        userRef.block(stranger);
}

    private MessageAdapter getMessageAdapter() {
        return (MessageAdapter) mView.getRecyclerView().getAdapter();
    }

    private Client<DataChange<Message>> createChatEventHandler(MessageAdapter adapter) {
        return dataChange -> {
            Log.d(TAG, String.format("Calling onListChanged with change: [%s] and messageText: [%s]",
                    dataChange.getEvent().toString(), dataChange.getItem().getText()));

            adapter.onListChanged(dataChange);
        };
    }

    public void onDestroy() {
        //We no longer want updates from the old chat. Remove us as a client from the old chat.
        if (chatEventHandler != null) {
            Log.d(TAG, "Removing old chatEventHandler, toString(): " + chatEventHandler);
            DatabaseHandler.get(mChat).unbindMessages(chatEventHandler);
            chatEventHandler = null;
        }
    }

    public void onChangeNameClicked(Context context, String chatName){
        LocalStorage.INSTANCE.saveSettings(context, mChat.getId(), chatName);
    }

    private void smoothScrollToBottomOfList(Message message) {
        RecyclerView recyclerView = mView.getRecyclerView();
        int scrollOffset = recyclerView.computeVerticalScrollOffset();

        int verticalRange = recyclerView.computeVerticalScrollRange();
        int verticalExtent = recyclerView.computeVerticalScrollExtent();

        int scrollHeight = verticalRange - verticalExtent;

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
}
