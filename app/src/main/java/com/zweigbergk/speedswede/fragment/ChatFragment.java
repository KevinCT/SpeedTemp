package com.zweigbergk.speedswede.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.pathmenu.FloatingActionMenu;
import com.zweigbergk.speedswede.pathmenu.SubActionButton;
import com.zweigbergk.speedswede.presenter.ChatFragmentPresenter;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import java.util.Timer;
import java.util.TimerTask;

import static com.zweigbergk.speedswede.Constants.CHAT_PARCEL;

public class ChatFragment extends Fragment implements ChatFragmentView, Client<String> {
    private static final String TAG = ChatFragment.class.getSimpleName().toUpperCase();

    private RecyclerView chatRecyclerView;
    private EditText mInputBox;

    private boolean isLocalUserFirstUser;
    private ArcMenu arcMenu;
    private Button btnLikeChat;

    private HashMap<Integer, View> arcComponents;
    private FloatingActionMenu pathMenu;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    SubActionButton.Builder itemBuilder;


    //TODO presenter between interactor and fragment
    private ChatFragmentPresenter mPresenter;

    public ChatFragment() {
        Log.d(TAG, "Creating a ChatFragment :)");
        mPresenter = new ChatFragmentPresenter(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ChatFragment.onCreate()");

        getActivity().invalidateOptionsMenu();
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
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Log.d(TAG, "onCreateView");

        view.findViewById(R.id.fragment_chat_post_message).setOnClickListener(this::onButtonClick);

        chatRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_chat_recycler_view);


        mInputBox = (EditText) view.findViewById(R.id.fragment_chat_message_text);

        getActivity().findViewById(R.id.sliding_layout).setOnClickListener(v -> pathMenu.close(true));

        return view;
    }

    private SubActionButton createPathButton(int resId) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setImageDrawable(scaleImage(getResources().getDrawable(resId), 2));
        return itemBuilder.setContentView(imageView).build();
    }

    public Drawable scaleImage(Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = new BitmapDrawable(getResources(), bitmapResized);

        return image;

    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    View btnShowActions = parent().findViewById(R.id.show_actions);
                    if (btnShowActions != null) {
                        stoptimertask();

                        SubActionButton leaveButton = createPathButton(R.drawable.ic_trashcan);
                        SubActionButton blockButton = createPathButton(R.drawable.ic_lock);

                        pathMenu = new FloatingActionMenu.Builder(getActivity())
                                .setRadius(getResources().getDimensionPixelSize(R.dimen.path_menu_radius))
                                .addSubActionView(blockButton)
                                .addSubActionView(leaveButton)
                                .setStartAngle(112)
                                .setEndAngle(158)
                                .attachTo(btnShowActions)
                                .build();
                    }
                });
            }};
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer.schedule(timerTask, 0, 50); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
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
    public void openLanguageFragment() {
        ((ChatActivity) getActivity()).openLanguageFragment();
    }

    @Override
    public void clearInputField() {
        mInputBox.setText("");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
<<<<<<< 30d3d8dbf95c6005a928c214246554e6bcbe3d41

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
                .setTitle(R.string.confirm_block_user)
                .setMessage(R.string.block_user_text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    dialog.dismiss();
                    DatabaseHandler.getReference(activeUser).block(otherUser);
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
                    parent().popBackStack();
                })
                .setNegativeButton(android.R.string.no, ((dialog, which) -> dialog.dismiss()))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void setIcon() {
        if(!hasLocalUserLiked()) {
            System.out.println("hasLocalUserLiked() = " + hasLocalUserLiked());
            btnLikeChat.setBackgroundResource(R.drawable.ic_thumb_up_white_24dp);
        }

        else if(hasLocalUserLiked() && !hasOtherUserLiked()) {
            System.out.println("hasLocalUserLiked() = " + hasLocalUserLiked());
            btnLikeChat.setBackgroundResource(R.drawable.com_facebook_button_like_icon_selected);
        }

        else if(hasBothUsersLiked()) {
            System.out.println("hasBothUsersLiked() = ");
            btnLikeChat.setBackgroundResource(R.drawable.com_facebook_button_icon_white);

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

        } else if (hasLocalUserLiked()) {
            setLikeForLocalUser(false);
            setIcon();

        } else {
            showLikeChatConfirmationDialog();
            setIcon();
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
        pathMenu.close(false);


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

    private Boolean hasLocalUserLiked() {
        isLocalUserFirstUser();
        Chat chat = mPresenter.getChat();
        System.out.println();
        if(isLocalUserFirstUser) {
            return chat.hasFirstUserLiked();
        } else {
            return chat.hasSecondUserLiked();
        }
    }

    private Boolean hasOtherUserLiked() {
        isLocalUserFirstUser();
        System.out.println("hasOtherUserLiked() - Is local user first user? " + isLocalUserFirstUser);
        Chat chat = mPresenter.getChat();
        if(isLocalUserFirstUser) {
            return chat.hasSecondUserLiked();
        } else {
            return chat.hasFirstUserLiked();
        }
    }

    @NonNull
    private Boolean hasBothUsersLiked() {
        Chat chat = mPresenter.getChat();

        return (chat.hasFirstUserLiked() && chat.hasSecondUserLiked());
    }

    private void setLikeForLocalUser(Boolean likeStatus) {
        isLocalUserFirstUser();
        System.out.println("setLikeForLocalUser() - Is local user first user? " + isLocalUserFirstUser);
        if(isLocalUserFirstUser) {

            DatabaseHandler.getReference(mPresenter.getChat()).setLikeStatusForFirstUser(likeStatus);
            //mPresenter.getChat().setLikeStatusFirstUser(likeStatus);
            setIcon();
        } else {

            DatabaseHandler.getReference(mPresenter.getChat()).setLikeStatusForSecondUser(likeStatus);
            //mPresenter.getChat().setLikeStatusSecondUser(likeStatus);
            setIcon();
        }
    }

    private void isLocalUserFirstUser() {
        User activeUser = DatabaseHandler.getActiveUser();
        DatabaseHandler.getReference(activeUser).pull().then(user -> {
            isLocalUserFirstUser = user.equals(mPresenter.getChat().getFirstUser());
        } );
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

    @Override
    public void useContext(Client<Context> client) {
        client.supply(getContext());
    }

    @Override
    public ImageView getImageView() {
        return new ImageView(getActivity());
    }

    @Override
    public ChatActivity getParent() {
        return parent();
    }
}