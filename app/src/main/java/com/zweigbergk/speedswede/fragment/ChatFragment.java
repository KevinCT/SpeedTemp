package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.adapter.MessageAdapter;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.ConversationEvent;
import com.zweigbergk.speedswede.service.DatabaseHandler;

import java.util.Date;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;

    public static final String DUMMY_CHAT_UID = "Chat123";

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initializeRecyclerView(view);

        view.findViewById(R.id.fragment_chat_post_message).setOnClickListener(this::onButtonClick);


        return view;
    }

    private void onButtonClick(View view) {
        EditText chatMessageText = ((EditText) this.getView().findViewById(R.id.fragment_chat_message_text));
        String messageText = chatMessageText.getText().toString();

        Message dummyMessage = new Message(Constants.TEST_USER_NAME, messageText, (new Date()).getTime());
        DatabaseHandler.INSTANCE.postMessageToChat(DUMMY_CHAT_UID, dummyMessage);
        chatMessageText.setText("");
    }

    private void initializeRecyclerView(View view) {
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        MessageAdapter adapter = new MessageAdapter();
        chatRecyclerView.setAdapter(adapter);
        DatabaseHandler.INSTANCE.registerConversationListener(DUMMY_CHAT_UID, adapter::onListChanged);

        adapter.addEventCallback(ConversationEvent.MESSAGE_ADDED, this::smoothScrollToBottomOfList);
    }

    private void smoothScrollToBottomOfList(Message message) {
        //Only scroll to the bottom if the new message was posted by us.
        if (message.getUid().equals(DatabaseHandler.INSTANCE.getLoggedInUser().getDisplayName())) {
            chatRecyclerView.post(() -> chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount() - 1));
        }
    }
}