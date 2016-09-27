package com.zweigbergk.speedswede.coreTest;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.TestFactory;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ChatTest {

    private Chat chat;

    private User sir;
    private User lord;
    private User igor;

    @Before
    public void initialize() {
        sir = TestFactory.mockUser(TestFactory.USER_1_NAME, TestFactory.USER_1_ID);
        lord = TestFactory.mockUser(TestFactory.USER_2_NAME, TestFactory.USER_2_ID);
        igor = TestFactory.mockUser(TestFactory.USER_3_NAME, TestFactory.USER_3_ID);

        chat = new Chat(sir, lord);
    }

    @Test
    public void stableState() {
        assertTrue(chat.getConversation() != null);
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
        List<Message> conversation = chat.getConversation();
        assertTrue(conversation != null);

        String messageText = "TestMessage: gettingMessages";
        chat.postMessage(sir, new Message("uid_sir", messageText));

        boolean foundMessage = false;
        conversation = chat.getConversation();
        for (Message m : conversation) {
            if (m.getText().equals(messageText)) {
                foundMessage = true;
            }
        }
        assertTrue(foundMessage);
    }
}
