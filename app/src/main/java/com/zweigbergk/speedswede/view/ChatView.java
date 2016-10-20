package com.zweigbergk.speedswede.view;

import android.content.Context;
import android.view.View;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;

public interface ChatView {
    void displayChat(Chat chat);
    void popBackStack();
    <T> T contextualize(ProviderMethod<T, Context> method);
    void useContext(CallerMethod<Context> method);
}
