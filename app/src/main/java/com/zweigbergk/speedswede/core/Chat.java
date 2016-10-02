package com.zweigbergk.speedswede.core;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Chat {

    private final User firstUser, secondUser;
    private List<Message> conversation;
    private String id;
    private long timeStamp;

    private boolean inactive;

    private Chat() {
        // Need one without args
        firstUser = new UserProfile("Dummy1", "Dummy1");
        secondUser = new UserProfile("Dummy2", "Dummy2");
    }

    public Chat(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        conversation = new ArrayList<>();
        timeStamp = (new Date()).getTime();

        id = Long.toString(firstUser.hashCode() * 5 +
                secondUser.hashCode() * 7 +
                timeStamp);
    }

    public Chat(String id, long timeStamp, List<Message> messages, User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.id = id;
        this.timeStamp = timeStamp;
        this.conversation = messages;
    }

    public boolean includesUser(User user) {
        return firstUser.equals(user) || secondUser.equals(user);
    }

    @Exclude
    public boolean isInactive() {
        return inactive;
    }

    @Exclude
    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public List<Message> getConversation() {
        List<Message> conversationClone = new ArrayList<>();
        for (Message m : conversation) {
            conversationClone.add(m.clone());
        }
        return conversationClone;
    }
    
    public String getId() {
        return id;
    }

    public long getIdAsLong() {
        return firstUser.hashCode() * 5 +
                secondUser.hashCode() * 7 +
                timeStamp;
    }

    public void postMessage(User user, Message message) throws IllegalArgumentException {
        if (!includesUser(user)) {
            throw new IllegalArgumentException("User provided ["+user.getUid()+"] is not a member of this chat.");
        }
        conversation.add(message);
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

    @Override
    public String toString() {
        return "First user: " + getFirstUser() + "\nSecond user: " + getSecondUser();
    }
}
