package com.zweigbergk.speedswede.core;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.util.Lists;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Chat {

    private User firstUser, secondUser;
    private List<Message> conversation;
    private String id;
    private long timeStamp;
    private long lastMessageTimeStamp;

    private String name;
    private boolean inactive;

    public Chat(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.name = Lists.randomPick(Constants.CHAT_NAMES);

        this.conversation = new ArrayList<>();
        timeStamp = (new Date()).getTime();

        id = firstUser.getUid() + "-" + secondUser.getUid();
    }

    public Chat(String id, String name, long timeStamp, List<Message> messages, User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.id = id;
        this.name = name;
        this.timeStamp = timeStamp;
        this.conversation = messages;
    }

    public boolean includesUser(User user) {
        return firstUser.equals(user) || secondUser.equals(user);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setFirstUser(User user) {
        this.firstUser = user;
    }

    public void setSecondUser(User user) {
        this.secondUser = user;
    }

    @Exclude
    public long getIdAsLong() {
        return firstUser.hashCode() * 5 +
                secondUser.hashCode() * 7 +
                timeStamp;
    }

    public Message getLatestMessage() {
        return conversation.size() > 0 ? conversation.get(conversation.size() - 1) : null;
    }

    public void postMessage(User user, Message message) throws IllegalArgumentException {
        if (!includesUser(user)) {
            throw new IllegalArgumentException(String.format("User provided [%s] is invert a member of this chat.", user.getUid()));
        }
        lastMessageTimeStamp = (new Date()).getTime();
        conversation.add(message);
    }

    public String getReadableTime() {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH).format(new Date(timeStamp));
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

        return otherChat.getId().equals(this.getId());
    }

    @Override
    public String toString() {
        if (getFirstUser() == null || getSecondUser() == null) {
            return "[Chat toString] Null user";
        }
        return "[Chat toString] First user: " + getFirstUser().getDisplayName() + "\nSecond user: " + getSecondUser().getDisplayName();
    }
}
