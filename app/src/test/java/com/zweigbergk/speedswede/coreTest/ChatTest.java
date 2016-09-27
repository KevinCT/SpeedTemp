package com.zweigbergk.speedswede.coreTest;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.TestFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ChatTest {

    private Chat chat;
    private User sir;
    private User lord;


    @Before
    public void initialize() {
        chat = new Chat(sir, lord);
        sir = TestFactory.mockUser(TestFactory.USER_1_NAME, TestFactory.USER_1_ID);
        lord = TestFactory.mockUser(TestFactory.USER_2_NAME, TestFactory.USER_2_ID);
    }

    @Test
    public void testStableState() {
        assertTrue(chat.getConversation() != null);
    }

    @Test
    public void testHasUsers() {
        assertTrue(chat.includesUser(sir) && chat.includesUser(lord));
    }
}
