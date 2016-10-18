package com.zweigbergk.speedswede.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.eyecandy.ArcMenu;
import com.zweigbergk.speedswede.presenter.ChatFragmentPresenter;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.collection.Point;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import static com.zweigbergk.speedswede.Constants.CHAT_PARCEL;

public class ChatFragment extends Fragment implements ChatFragmentView, Client<String> {
    private static final String TAG = ChatFragment.class.getSimpleName().toUpperCase();

    private RecyclerView chatRecyclerView;
    private EditText mInputBox;

    private ArcMenu arcMenu;

    Point arcLayoutPosition = new Point(0, 0);

    //TODO presenter between interactor and fragment
    private ChatFragmentPresenter mPresenter;

    public ChatFragment() {
        Log.d(TAG, "Creating a ChatFragment :)");
        mPresenter = new ChatFragmentPresenter(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
        Log.d(TAG, "ChatFragment.onCreate()");
    }

    private void checkSavedState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Chat chat = savedInstanceState.getParcelable(CHAT_PARCEL);

            Log.d(TAG, "Found old chat. Setting it.");
            setChat(chat);
            if (chat != null) {
                Log.d(TAG, chat.toString());
            }
        }
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkSavedState(savedInstanceState);
        Log.d(TAG, "ChatFragment.onActivityCreated()");

        mPresenter.invalidate();
    }

    @Override
    public void onResume() {
        super.onResume();
        ActionBar parentToolbar = ((AppCompatActivity) getActivity()).getSupportActionBar();

        if (parentToolbar != null) {
            parentToolbar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Log.d(TAG, "onCreateView");

        view.findViewById(R.id.fragment_chat_post_message).setOnClickListener(this::onButtonClick);

        chatRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);
        mInputBox = (EditText) view.findViewById(R.id.fragment_chat_message_text);

        arcMenu = new ArcMenu(parent());

        return view;
    }

    private Point getMenuItemPosition(int resId) {
        View myActionView = parent().getToolbar().findViewById(resId);
            int[] location = new int[2];
            myActionView.getLocationOnScreen(location);

            int x = location[0];
            int y = location[1];
        Log.d(TAG, Stringify.curlyFormat("getMenuItemPosition(): x: {x}, y: {y}", x, y));
        return new Point(x, y);
    }

    public void setChat(Chat newChat) {
        mPresenter.setChat(newChat);
    }

    private void onButtonClick(View view) {
        mPresenter.onClickSend();
    }

    @Override
    public String getInputText() {
        return mInputBox.getText().toString();
    }

    @Override
    public void openLanguageFragment() {
        ((ChatActivity) getActivity()).openLanguageFragment();
    }

    @Override
    public void clearInputField() {
        mInputBox.setText("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        inflater.inflate(R.menu.new_menu_chat, menu);
        super.onCreateOptionsMenu(menu,inflater);

        Button btnBlockUser = arcMenu.getButton(R.id.btn_arc_menu_block_user);
        btnBlockUser.setOnClickListener(v -> showBlockConfirmationDialog());

        Button btnLeaveChat = arcMenu.getButton(R.id.btn_arc_menu_leave_chat);
        btnLeaveChat.setOnClickListener(v -> showLeaveChatConfirmationDialog());
    }

    private void showBlockConfirmationDialog() {
        User activeUser = DatabaseHandler.getActiveUser();
        User otherUser = mPresenter.getChat().getOtherUser(activeUser);


        new AlertDialog.Builder(getContext())
                .setTitle(R.string.confirm_block_user)
                .setMessage(R.string.block_user_text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    DatabaseHandler.getReference(activeUser).block(otherUser);
                    arcMenu.onDestroy();
                    parent().popBackStack();
                })
                .setNegativeButton(android.R.string.no, ((dialog, which) -> dialog.dismiss()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void showLeaveChatConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.exit_chat)
                .setMessage(R.string.exit_chat_text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    mPresenter.terminateChat();
                    arcMenu.onDestroy();
                    parent().popBackStack();
                })
                .setNegativeButton(android.R.string.no, ((dialog, which) -> dialog.dismiss()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_actions:
                    Log.d(TAG, "onOptionsItemSelected: setting position...");
                    arcLayoutPosition = getMenuItemPosition(R.id.show_actions);
                    arcMenu.setOrigin(arcLayoutPosition);
                arcMenu.update();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");

        mPresenter.onDestroy();
        arcMenu.onDestroy();

        super.onDestroy();
    }

    private ChatActivity parent() {
        return (ChatActivity) getActivity();
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

    @Override
    public void supply(String s) {
        getActivity().setTitle(s);
        mPresenter.onChangeNameClicked(getActivity().getBaseContext(), s);
    }
}