package com.zweigbergk.speedswede.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.presenter.ChatFragmentPresenter;
import com.zweigbergk.speedswede.util.CallerMethod;
import com.zweigbergk.speedswede.util.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatFragmentView;

public class ChatFragment extends Fragment implements ChatFragmentView {
    public static final String TAG = ChatFragment.class.getSimpleName().toUpperCase();

    private RecyclerView chatRecyclerView;
    private EditText mInputBox;

    //TODO presenter between interactor and fragment
    private ChatFragmentPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPresenter = new ChatFragmentPresenter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        view.findViewById(R.id.fragment_chat_post_message).setOnClickListener(this::onButtonClick);

        chatRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);
        mInputBox = (EditText) view.findViewById(R.id.fragment_chat_message_text);

        return view;
    }

    public void setChat(Chat newChat) {
        mPresenter.onChatChanged(newChat);
    }

    private void onButtonClick(View view) {
        mPresenter.onClickSend();
    }

    @Override
    public String getInputText() {
        return mInputBox.getText().toString();
    }

    @Override
    public void clearInputField() {
        mInputBox.setText("");
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
                mPresenter.onBanClicked();
                return true;
            case R.id.changeLangauge:
                mPresenter.onChangeLanguageClicked();
                return true;
            case R.id.exitChat:
                mPresenter.terminateChat();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public RecyclerView getRecyclerView() {
        return chatRecyclerView;
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        chatRecyclerView.setLayoutManager(layoutManager);
    }

    @Override
    public <T> T contextualize(ProviderMethod<T, Context> method) {
        return method.call(getContext());
    }

    @Override
    public void useActivity(CallerMethod<ChatActivity> method) {
        method.call((ChatActivity) getActivity());
    }
}