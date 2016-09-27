package com.zweigbergk.speedswede.core;

import android.support.annotation.NonNull;

import java.util.List;

public class Chat {

    private final User mFirstUser, mSecondUser;

    public Chat(@NonNull User firstUser, @NonNull User secondUser) {
        mFirstUser = firstUser;
        mSecondUser = secondUser;
    }
    public boolean includesUser(User user) {
        return false;
    }

    public User getFirstUser() {
        return mFirstUser;
    }

    public User getSecondUser() {
        return mSecondUser;
    }

    public List<Message> getConversation() {
        return null;
    }
}
