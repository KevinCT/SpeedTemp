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

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.adapter.MessageAdapter;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.DatabaseEvent;
import com.zweigbergk.speedswede.service.DatabaseHandler;

import java.util.Date;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;

    public static final String DUMMY_CHAT_UID = "Chat123";

    public String mCurrentChatId;

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

        if (mCurrentChatId == null || mCurrentChatId.equals("")) {
            mCurrentChatId = "privateChatUser_"+DatabaseHandler.INSTANCE.getLoggedInUser().getUid();
        }
//        mCurrentChatId = "privateChatUser_742nxCA9qvUF4ZIRGqA9sWfgooH2"; // Andreas facebook-id

        initializeRecyclerView(view);

        view.findViewById(R.id.fragment_chat_post_message).setOnClickListener(this::onButtonClick);

        return view;
    }

    private void onButtonClick(View view) {
        EditText chatMessageText = ((EditText) this.getView().findViewById(R.id.fragment_chat_message_text));
        String messageText = chatMessageText.getText().toString();

        if (messageText.length() > 0) {
            Message message = new Message(DatabaseHandler.INSTANCE.getLoggedInUser().getUid(), messageText, (new Date()).getTime());
            DatabaseHandler.INSTANCE.postMessageToChat(mCurrentChatId, message);
//        DatabaseHandler.INSTANCE.postMessageToChat(DUMMY_CHAT_UID, message);
            chatMessageText.setText("");
        }

    }

    private void initializeRecyclerView(View view) {
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        chatRecyclerView.setLayoutManager(layoutManager);
        layoutManager.setStackFromEnd(true);

        MessageAdapter adapter = new MessageAdapter();
        chatRecyclerView.setAdapter(adapter);
        DatabaseHandler.INSTANCE.registerConversationListener(mCurrentChatId, adapter::onListChanged);

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
        if ((message.getUid() != null && message.getUid().equals(DatabaseHandler.INSTANCE.getActiveUserId()))
                || (scrollHeight - scrollOffset < chatRecyclerView.getHeight())) {
            chatRecyclerView.post(() -> chatRecyclerView.smoothScrollToPosition(chatRecyclerView.getAdapter().getItemCount() - 1));
        }

    }
}