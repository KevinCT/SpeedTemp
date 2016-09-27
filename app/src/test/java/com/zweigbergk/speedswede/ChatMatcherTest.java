package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.TestFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChatMatcherTest {

    private Chat chat;
    private User sir;
    private User lord;
    private ChatMatcher matcher = ChatMatcher.INSTANCE;

    @Before
    public void initialize() {
        sir = TestFactory.mockUser(TestFactory.USER_1_NAME, TestFactory.USER_1_ID);
        lord = TestFactory.mockUser(TestFactory.USER_2_NAME, TestFactory.USER_2_ID);

        chat = new Chat(sir, lord);

        ChatMatcher.INSTANCE.clear();
    }

    @Test
    public void addUser() {
        matcher.pushUser(sir);

        assertTrue(matcher.hasUserInPool(sir));
    }

    @Test
    public void hasUserInPool() {
        assertFalse(matcher.hasUserInPool(sir));

        matcher.pushUser(sir);

        assertTrue(matcher.hasUserInPool(sir));
    }

    @Test
    public void removeUser() {
        matcher.pushUser(sir);

        matcher.removeUser(sir);

        assertFalse(matcher.hasUserInPool(sir));
    }
}
