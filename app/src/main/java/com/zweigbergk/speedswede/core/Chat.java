package com.zweigbergk.speedswede.core;

import com.google.firebase.database.Exclude;
import com.zweigbergk.speedswede.interactor.ChatInteractor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Chat {

    private final User firstUser, secondUser;
    private List<Message> conversation;
    private String id;
    private long timeStamp;
    private long lastMessageTimeStamp;
    private String chatName;

    private ChatInteractor chatInteractor;
    private boolean inactive;

    private Chat() {
        // Need one without args
        firstUser = new UserProfile("Dummy1", "Dummy1");
        secondUser = new UserProfile("Dummy2", "Dummy2");
    }

    public Chat(User firstUser, User secondUser) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.chatName = chatInteractor.getRandomChatName();

        conversation = new ArrayList<>();
        timeStamp = (new Date()).getTime();

        id = firstUser.getUid() + "-" + secondUser.getUid();
    }

    public Chat(String id, long timeStamp, List<Message> messages, User firstUser, User secondUser, String chatName) {
        this.firstUser = firstUser;
        this.secondUser = secondUser;

        this.chatName = chatName;
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

    public String getChatName() {
        return chatName;
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

    @Exclude
    public long getIdAsLong() {
        return firstUser.hashCode() * 5 +
                secondUser.hashCode() * 7 +
                timeStamp;
    }

    public void postMessage(User user, Message message) throws IllegalArgumentException {
        if (!includesUser(user)) {
            throw new IllegalArgumentException(String.format("User provided [%s] is not a member of this chat.", user.getUid()));
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
