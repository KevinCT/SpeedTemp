package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.adapter.NewMessageAdapter;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.service.DatabaseHandler;

import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment {
    private RecyclerView chatRecyclerView;

    private static final int SCROLL_DOWN_DELAY = 10;

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
        Message dummyMessage = new Message("Peter", "Ny text igen", (new Date()).getTime());
        DatabaseHandler.INSTANCE.postMessage(dummyMessage);
    }

    private void initializeRecyclerView(View view) {
        chatRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        DatabaseHandler.INSTANCE.fetchConversation(this::setAdapterMessageList);
        chatRecyclerView.addOnLayoutChangeListener(this::reactToKeyboardPopup);

    }

    private void setAdapterMessageList(List<Message> list) {
        chatRecyclerView.setAdapter(new NewMessageAdapter(list));
    }

    private void reactToKeyboardPopup(View view, int left, int top, int right, int bottom,
                                      int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (bottom < oldBottom) {
            view.post(this::scrollToBottomOfList);
        }
    }

    private void scrollToBottomOfList() {
        chatRecyclerView.scrollToPosition(chatRecyclerView.getAdapter().getItemCount() - 1);
    }
}