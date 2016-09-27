package com.zweigbergk.speedswede.view;

import android.content.Context;

import com.zweigbergk.speedswede.util.Client;

public interface ChatView {
    /** Used if a client needs access to the application context */
    void useContextTo(Client<Context> client);
}
