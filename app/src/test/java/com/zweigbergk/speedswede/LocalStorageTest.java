package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.local.LocalStorage;
import com.zweigbergk.speedswede.core.local.UserData;
import com.zweigbergk.speedswede.util.TestFactory;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class LocalStorageTest {

    private Chat chat;
    private User sir;
    private User lord;

    @Before
    public void initialize() {
        chat = new Chat(sir, lord);
        sir = TestFactory.mockUser(TestFactory.USER_1_NAME, TestFactory.USER_1_ID);
        lord = TestFactory.mockUser(TestFactory.USER_2_NAME, TestFactory.USER_2_ID);

        ChatMatcher.INSTANCE.clear();
    }

    @Test
    public void saveAndLoadChat() {
        LocalStorage.saveChat(chat);

        UserData userData = LocalStorage.getUserData(sir);
        assertTrue(userData != null);

        Chat chatFromStorage = LocalStorage.getUserData(sir).getChatByUid(chat.getId());
        assertTrue(chatFromStorage.equals(chat));
    }
}
