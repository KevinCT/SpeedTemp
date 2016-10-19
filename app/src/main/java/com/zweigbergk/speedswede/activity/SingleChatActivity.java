package com.zweigbergk.speedswede.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.eyecandy.PathMenu;
import com.zweigbergk.speedswede.eyecandy.TransparentLayout;
import com.zweigbergk.speedswede.pathmenu.SubActionButton;
import com.zweigbergk.speedswede.presenter.ChatFragmentPresenter;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import static com.zweigbergk.speedswede.Constants.CHAT_PARCEL;

public class SingleChatActivity extends AppCompatActivity implements ChatFragmentView {
    private static final String TAG = SingleChatActivity.class.getSimpleName().toUpperCase();

    //Activity stuff
    private Toolbar toolbar;


    //From ChatFragment
    private ChatFragmentPresenter mPresenter;


    private RecyclerView chatRecyclerView;
    private EditText mInputBox;

    private PathMenu pathMenu;
    private boolean isPathMenuOpened = false;

    SubActionButton.Builder itemBuilder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_chat);
        Log.d(TAG, "onCreate");

        setupToolbar();

        ((TransparentLayout) findViewById(R.id.activity_single_chat_root_layout))
                .onTouchRegistered(() -> {
                    if (isPathMenuOpened) {
                        pathMenu.close(true);
                    }
                });

        mPresenter = new ChatFragmentPresenter(this);

        loadChatFromIntent();

        findViewById(R.id.fragment_chat_post_message).setOnClickListener(this::onButtonClick);

        chatRecyclerView = (RecyclerView) findViewById(R.id.fragment_chat_recycler_view);
        mInputBox = (EditText) findViewById(R.id.fragment_chat_message_text);

        itemBuilder = new SubActionButton.Builder(this);

        pathMenu = new PathMenu(this, itemBuilder);
        pathMenu.addImageViewWithAction(getImageView(R.drawable.ic_lock2), v -> showBlockConfirmationDialog());
        pathMenu.addImageViewWithAction(getImageView(R.drawable.ic_direction), v -> showLeaveChatConfirmationDialog());

        pathMenu.create();
        pathMenu.addStateClient(isOpened -> isPathMenuOpened = isOpened);

        mPresenter.invalidate();
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        if (getSupportActionBar() != null) {
            final Drawable upArrow = getResources().getDrawable(R.drawable.ic_go_back_left_arrow);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void loadChatFromIntent() {
        Bundle bundle = getIntent().getExtras();
        Chat chat = bundle.getParcelable(Constants.CHAT_PARCEL);
        if (chat != null) {
            Log.d(TAG, "The chatID: " + chat.getId());
            setChat(chat);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_menu_chat, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState()");

        mPresenter.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            Chat chat = savedInstanceState.getParcelable(CHAT_PARCEL);

            Log.d(TAG, "Found old chat with ID: " + chat.getId());
            setChat(chat);
        }
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
        public void clearInputField() {
            mInputBox.setText("");
        }


        private void showBlockConfirmationDialog() {
            User activeUser = DatabaseHandler.getActiveUser();
            User otherUser = mPresenter.getChat().getOtherUser(activeUser);


            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.confirm_block_user))
                    .setMessage(getResources().getString(R.string.block_user_text))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        DatabaseHandler.getReference(activeUser).block(otherUser);
                        mPresenter.terminateChat();
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, ((dialog, which) -> dialog.dismiss()))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        private void showLeaveChatConfirmationDialog() {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.exit_chat))
                    .setMessage(getResources().getString(R.string.exit_chat_text))
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        dialog.dismiss();
                        mPresenter.terminateChat();
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, ((dialog, which) -> dialog.dismiss()))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    finish();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy()");

            mPresenter.onDestroy();
            pathMenu.close(false);


            super.onDestroy();
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
            return method.call(this);
        }

        @Override
        public ImageView getImageView(int resId) {
            ImageView view = new ImageView(this);
            view.setImageDrawable(getResources().getDrawable(resId));
            //view.setImageDrawable(resizeImage(getContext(), resId, 60, 60));
            return view;
        }
}
