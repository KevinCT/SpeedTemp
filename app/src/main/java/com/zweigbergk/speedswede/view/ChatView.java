package com.zweigbergk.speedswede.view;

import android.content.Context;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;

public interface ChatView {
    void displayChat(Chat chat);
    void popBackStack();
}
