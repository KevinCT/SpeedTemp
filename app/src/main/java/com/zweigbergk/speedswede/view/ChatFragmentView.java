package com.zweigbergk.speedswede.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewParent;
import android.widget.ImageView;

import com.zweigbergk.speedswede.activity.ChatActivity;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;

public interface ChatFragmentView {
    RecyclerView getRecyclerView();
    void setLayoutManager(RecyclerView.LayoutManager layoutManager);
    <T> T contextualize(ProviderMethod<T, Context> method);
    void useActivity(CallerMethod<ChatActivity> method);
    void clearInputField();
    String getInputText();
    void openLanguageFragment();
    void useContext(Client<Context> client);
    ImageView getImageView(int resId);
    ChatActivity getParent();
}
