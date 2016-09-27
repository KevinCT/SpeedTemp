package com.zweigbergk.speedswede.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat {

    private final User mFirstUser, mSecondUser;
    private List<Message> mConversation;
    private String mId;
    private long mTimeStamp;

    public Chat(User firstUser, User secondUser) {
        mFirstUser = firstUser;
        mSecondUser = secondUser;

        mConversation = new ArrayList<>();
        mId = Long.toString(firstUser.getUid().hashCode() * 17 + secondUser.getUid().hashCode() * 31);

        mTimeStamp = (new Date()).getTime();
    }
    public boolean includesUser(User user) {
        return mFirstUser.equals(user) || mSecondUser.equals(user);
    }

    public User getFirstUser() {
        return mFirstUser;
    }

    public User getSecondUser() {
        return mSecondUser;
    }

    public long getTimeStamp() {
        return mTimeStamp;
    }

    public List<Message> getConversation() {
        return null;
    }
    
    public String getId() {
        return mId;
    }

    public void postMessage(User user, Message message) throws IllegalArgumentException {

    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!other.getClass().equals(this.getClass())) {
            return false;
        }
        Chat otherChat = (Chat) other;

        return this.getTimeStamp() == otherChat.getTimeStamp() &&
                this.getFirstUser().equals(otherChat.getFirstUser()) &&
                this.getSecondUser().equals(otherChat.getSecondUser()) &&
                this.getConversation().equals(otherChat.getConversation());
    }
}
