package com.zweigbergk.speedswede.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.zweigbergk.speedswede.adapter.MessageAdapter;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.LocalStorage;
import com.zweigbergk.speedswede.interactor.BanInteractor;
import com.zweigbergk.speedswede.methodwrapper.Client;
import com.zweigbergk.speedswede.util.Time;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import static com.zweigbergk.speedswede.Constants.CHAT_PARCEL;


public class ChatFragmentPresenter {
    public static final String TAG = ChatFragmentPresenter.class.getSimpleName().toUpperCase();

    private BanInteractor mBanInteractor;

    private ChatFragmentView mView;

    private Chat mChat;

    public ChatFragmentPresenter(ChatFragmentView view){
        mView = view;
        mBanInteractor = new BanInteractor();
    }
    public Chat getChat(){
        return mChat;
    }

    public void setChat(Chat chat) {
        if (mChat != null) {
            //We no longer want updates from the old chat. Remove us as a client from the old chat.
            DatabaseHandler.get(mChat).unbindMessageClient(handleChatEvent);

            mChat = chat;
            invalidate();
        } else {
            mChat = chat;
        }
    }

    private void initializeRecyclerView() {
        RecyclerView recyclerView = mView.getRecyclerView();

        LinearLayoutManager layoutManager = mView.contextualize(LinearLayoutManager::new);
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);

        MessageAdapter adapter = new MessageAdapter();
        recyclerView.setAdapter(adapter);

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Clicked :D");
            }
        });

        adapter.addEventCallback(DatabaseEvent.ADDED, this::smoothScrollToBottomOfList);
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

    /**
     * Tells the presenter to update the state of the view
     */
    public void invalidate() {
        initializeRecyclerView();

        getMessageAdapter().clear();

        //We want updates from the new chat! Add us as a client to that one :)
        DatabaseHandler.get(mChat).bindMessageClient(handleChatEvent);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(CHAT_PARCEL, mChat);
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
        DatabaseHandler.hasUsers(mChat).then(
                result -> {
                    if(!result) {
                        DatabaseHandler.get(mChat).remove();
                    }
                }
        );
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
        mView.openLanguageFragment();
    }

    private MessageAdapter getMessageAdapter() {
        return (MessageAdapter) mView.getRecyclerView().getAdapter();
    }

    private final Client<DataChange<Message>> handleChatEvent = dataChange -> {
        Log.d(TAG, String.format("Calling onListChanged with change: [%s] and messageText: [%s]",
                dataChange.getEvent().toString(), dataChange.getItem().getText()));

        ((MessageAdapter) mView.getRecyclerView().getAdapter()).onListChanged(dataChange);
    };

    public void onChangeNameClicked(Context context, String chatName){
        LocalStorage.INSTANCE.saveSettings(context, mChat.getId(), chatName);
    }
}
