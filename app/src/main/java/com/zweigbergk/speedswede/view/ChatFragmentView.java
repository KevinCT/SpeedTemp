package com.zweigbergk.speedswede.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;

public interface ChatFragmentView {
    RecyclerView getRecyclerView();
    void setLayoutManager(RecyclerView.LayoutManager layoutManager);
    <T> T contextualize(ProviderMethod<T, Context> method);
    void clearInputField();
    String getInputText();
    ImageView getImageView(int resId);
}
