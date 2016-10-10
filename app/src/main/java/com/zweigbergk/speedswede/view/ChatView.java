package com.zweigbergk.speedswede.view;

import com.zweigbergk.speedswede.core.Chat;

public interface ChatView {
    void displayChat(Chat chat);
    void popBackStack();
}
