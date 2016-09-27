package com.zweigbergk.speedswede.core;

import android.support.annotation.NonNull;

import java.util.List;

public class Chat {

    private final User mFirstUser, mSecondUser;

    private String mUid;

    public Chat(@NonNull User firstUser, @NonNull User secondUser) {
        mFirstUser = firstUser;
        mSecondUser = secondUser;

        mUid = Long.toString(firstUser.getUid().hashCode() * 17 + secondUser.getUid().hashCode() * 31);
    }
    public boolean includesUser(User user) {
        return false;
    }

    public User getFirstUser() {
        return null;
    }

    public User getSecondUser() {
        return null;
    }

    public List<Message> getConversation() {
        return null;
    }

    public String getUid() {
        return mUid;
    }
}
