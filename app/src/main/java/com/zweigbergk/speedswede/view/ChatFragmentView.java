package com.zweigbergk.speedswede.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;

public interface ChatFragmentView {
    RecyclerView getRecyclerView();
    <T> T contextualize(ProviderMethod<T, Context> method);
    void clearInputField();
    String getInputText();
}
