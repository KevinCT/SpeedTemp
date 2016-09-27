package com.zweigbergk.speedswede.core;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private final User mFirstUser, mSecondUser;
    private List<Message> mConversation;
    private String mUid;

    public Chat(User firstUser, User secondUser) {
        mFirstUser = firstUser;
        mSecondUser = secondUser;

        mConversation = new ArrayList<>();
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

    public void postMessage(User user, Message message) throws IllegalArgumentException {

    }
}
