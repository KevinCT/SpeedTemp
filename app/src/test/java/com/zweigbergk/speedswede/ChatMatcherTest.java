package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.factory.ChatFactory;

import org.junit.Before;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChatMatcherTest {

    public final static String USER_1_NAME = "Sir";
    public final static String USER_2_NAME = "Lord";
    public final static String USER_3_NAME = "Igor";

    public final static String USER_1_ID = "uid_user1";
    public final static String USER_2_ID = "uid_user2";
    public final static String USER_3_ID = "uid_user3";

    private Chat chat;
    private User sir;
    private User lord;
    private ChatMatcher matcher = ChatMatcher.INSTANCE;

    @Before
    public void initialize() {
//        sir = ChatFactory.mockUser(ChatFactory.USER_1_NAME, ChatFactory.USER_1_ID);
//        lord = ChatFactory.mockUser(ChatFactory.USER_2_NAME, ChatFactory.USER_2_ID);

        chat = new Chat(sir, lord);

        ChatMatcher.INSTANCE.clear();
    }
}
