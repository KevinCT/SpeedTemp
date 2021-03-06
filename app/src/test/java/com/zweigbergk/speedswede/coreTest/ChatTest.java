package com.zweigbergk.speedswede.coreTest;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;

import org.junit.Before;
import org.junit.Test;

import com.zweigbergk.speedswede.util.collection.ListExtension;

import static org.junit.Assert.assertTrue;

public class ChatTest {

    private Chat chat;

    private User sir;
    private User lord;
    private User igor;

    @Before
    public void initialize() {
//        sir = ChatFactory.mockUser(ChatFactory.USER_1_NAME, ChatFactory.USER_1_ID);
//        lord = ChatFactory.mockUser(ChatFactory.USER_2_NAME, ChatFactory.USER_2_ID);
//        igor = ChatFactory.mockUser(ChatFactory.USER_3_NAME, ChatFactory.USER_3_ID);

        chat = new Chat(sir, lord);
    }

    @Test
    public void stableState() {
        assertTrue(chat.getMessages() != null);
    }

    @Test
    public void hasUsers() {
        assertTrue(chat.includesUser(sir) && chat.includesUser(lord));
    }

    @Test
    public void postingMessages() {
        boolean hasException = false;
        try {
            chat.postMessage(sir, new Message("uid_sir", "Message 1"));
        } catch (IllegalArgumentException e) {
            hasException = true;
        }
        assertTrue(!hasException);

        hasException = false;
        try {
            chat.postMessage(igor, new Message("uid_igor", "Message 2"));
        } catch (IllegalArgumentException e) {
            hasException = true;
        }
        assertTrue(hasException);
    }

    @Test
    public void gettingMessages() {
        ListExtension<Message> conversation = chat.getMessages();
        assertTrue(conversation != null);

        String messageText = "TestMessage: gettingMessages";
        chat.postMessage(sir, new Message("uid_sir", messageText));

        boolean foundMessage = false;
        conversation = chat.getMessages();
        for (Message m : conversation) {
            if (m.getText().equals(messageText)) {
                foundMessage = true;
            }
        }
        assertTrue(foundMessage);
    }
}
