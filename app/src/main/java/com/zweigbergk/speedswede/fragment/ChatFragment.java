package com.zweigbergk.speedswede.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.zweigbergk.speedswede.core.UserProfile;
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

    private boolean isLocalUserFirstUser;
    private ArcMenu arcMenu;
    private Button btnLikeChat;

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

        btnLikeChat = arcMenu.getButton(R.id.btn_arc_menu_like_chat);
        setIcon();
        btnLikeChat.setOnClickListener(v  -> likeChatUpdate());


    }

    private void showBlockConfirmationDialog() {
        User activeUser = DatabaseHandler.getActiveUser();
        User otherUser = mPresenter.getChat().getOtherUser(activeUser);


        new AlertDialog.Builder(getContext())
                .setTitle("Block this user?")
                .setMessage("You will not be matched with him or her again.")
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
                .setTitle("Leave this chat?")
                .setMessage("You will not be able to come back to it.")
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

    private void setIcon() {
        if(!hasLocalUserLiked()) {
            btnLikeChat.setBackgroundResource(R.drawable.com_facebook_button_send_icon_blue);
        }

        else if(hasLocalUserLiked() && !hasOtherUserLiked()) {
            btnLikeChat.setBackgroundResource(R.drawable.com_facebook_button_like_icon_selected);
        }

        else if(hasBothUsersLiked()) {
            btnLikeChat.setBackgroundResource(R.drawable.com_facebook_button_icon_blue);
        }
    }

    private void likeChatUpdate() {

        if(hasBothUsersLiked()) {
            Chat chat = mPresenter.getChat();
            isLocalUserFirstUser();
            String facebookUserID = "";
            if(isLocalUserFirstUser) {
                UserProfile otherUser = (UserProfile) chat.getSecondUser();
                facebookUserID = otherUser.facebookUserID;
                System.out.println("facebookUserID = " + otherUser.facebookUserID); //returnerar samma id för båda användare
            } else {
                UserProfile otherUser = (UserProfile) chat.getFirstUser();
                otherUser.facebookUserID = otherUser.facebookUserID;
                System.out.println("facebookUserID = " + otherUser.facebookUserID);
            }
            String url = "http://www.facebook.com/" + facebookUserID;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);

        } else if (!hasLocalUserLiked()) {
            showLikeChatConfirmationDialog();
            setIcon();
        } else {
            if(hasLocalUserLiked()) {
                setLikeForLocalUser(false);
                setIcon();
            } else {
                setLikeForLocalUser(true);
                setIcon();
            }
        }

    }

    private void showLikeChatConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Like this chat?");
        builder.setMessage("If you like this chat, your messaging partner will be able to view your facebook profile");
        builder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            setLikeForLocalUser(true);
            setIcon();
            dialog.dismiss();
            arcMenu.onDestroy();
            parent().popBackStack();
        });

        builder.setNegativeButton(android.R.string.no, ((dialog, which) -> dialog.dismiss()));
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
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

    public Boolean hasLocalUserLiked() {
        isLocalUserFirstUser();
        Chat chat = mPresenter.getChat();
        if(isLocalUserFirstUser) {
            return chat.hasFirstUserLiked();
        } else {
            return chat.hasSecondUserLiked();
        }
    }

    public Boolean hasOtherUserLiked() {
        isLocalUserFirstUser();
        Chat chat = mPresenter.getChat();
        if(isLocalUserFirstUser) {
            return chat.hasSecondUserLiked();
        } else {
            return chat.hasFirstUserLiked();
        }
    }

    public Boolean hasBothUsersLiked() {
        Chat chat = mPresenter.getChat();
        System.out.println("First user like?" + chat.hasFirstUserLiked());
        System.out.println("Second user like?" + chat.hasSecondUserLiked());
        return (chat.hasFirstUserLiked() && chat.hasSecondUserLiked());
    }

    public void setLikeForLocalUser(Boolean likeStatus) {
        isLocalUserFirstUser();
        if(isLocalUserFirstUser) {
            DatabaseHandler.getReference(mPresenter.getChat()).setLikeStatusForFirstUser(likeStatus);

        } else {
            DatabaseHandler.getReference(mPresenter.getChat()).setLikeStatusForSecondUser(likeStatus);

        }
    }

    public void isLocalUserFirstUser() {
        User activeUser = DatabaseHandler.getActiveUser();
        DatabaseHandler.getReference(activeUser).pull().then(user -> {
            isLocalUserFirstUser = user.equals(mPresenter.getChat().getFirstUser());
        } );
    }

}