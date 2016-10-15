package com.zweigbergk.speedswede.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ogaclejapan.arclayout.ArcLayout;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.database.LocalStorage;
import com.zweigbergk.speedswede.eyecandy.AnimatorUtils;
import com.zweigbergk.speedswede.presenter.ChatFragmentPresenter;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import static com.zweigbergk.speedswede.Constants.CHAT_PARCEL;

public class ChatFragment extends Fragment implements ChatFragmentView, Client<String> {
    public static final String TAG = ChatFragment.class.getSimpleName().toUpperCase();

    private RecyclerView chatRecyclerView;
    private EditText mInputBox;

    private View menuLayout;
    private ArcLayout arcLayout;
    private FloatingActionButton fab;

    Toast toast = null;

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

        //Eyecandy
        menuLayout = view.findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) view.findViewById(R.id.arc_layout);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(this::onFabClick);

        for (int i = 0, size = arcLayout.getChildCount(); i < size; i++) {
            arcLayout.getChildAt(i).setOnClickListener(v -> showToast((Button) v));
            arcLayout.getChildAt(i).setBackgroundResource(R.drawable.com_facebook_close);
        }

        return view;
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
        inflater.inflate(R.menu.new_menu_chat,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.show_actions:
                if (menuLayout.getVisibility() == View.VISIBLE) {
                    hideMenu();
                } else {
                    showMenu();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        /*switch (item.getItemId()) {
            case R.id.blockUser:
                mPresenter.onBanClicked();
                mPresenter.terminateChat();
                mPresenter.removeLocalChatSettings(getActivity().getBaseContext());
                return true;
            case R.id.exitChat:
                mPresenter.terminateChat();
                return true;
            case R.id.changeChatName:
                openDialog();
                return true;
            case R.id.removeSettings:
                LocalStorage.INSTANCE.removeAllSettings(getActivity().getBaseContext());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
    }

    private void openDialog(){
        FragmentManager fragmentManager = getChildFragmentManager();
        DialogFragment dialogFragment = new DialogFragment ();
        dialogFragment.show(fragmentManager, "Sample Fragment");
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

    private void showToast(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Clicked: " + btn.getText();
        toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.show();

    }

    private void onFabClick(View v) {
        if (v.isSelected()) {
            hideMenu();
        } else {
            showMenu();
        }
        v.setSelected(!v.isSelected());
    }

    @SuppressWarnings("NewApi")
    private void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);

        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = arcLayout.getChildCount(); i < len; i++) {
            animList.add(createShowItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {

        java.util.List<Animator> animList = new java.util.ArrayList<>();

        for (int i = arcLayout.getChildCount() - 1; i >= 0; i--) {
            animList.add(createHideItemAnimator(arcLayout.getChildAt(i)));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {

        float dx = fab.getX() - item.getX();
        float dy = 0 - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        return anim;
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = fab.getX() - item.getX();
        float dy = 0 - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }
}