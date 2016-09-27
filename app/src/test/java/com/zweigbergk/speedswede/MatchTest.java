package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

import org.junit.Test;

/**
 * Created by FEngelbrektsson on 27/09/16.
 */
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MatchTest {

    private User user1 = new UserProfile("Kompis1", "Kompis1");
    private User user2 = new UserProfile("Kompis2", "Kompis2");
    private User user3 = new UserProfile("Kompis3", "Kompis3");
    private User user4 = new UserProfile("Kompis4", "Kompis4");
    private User user5 = new UserProfile("Kompis5", "Kompis5");


    @Test
    public void addUserToPool() {
        ChatMatcher.INSTANCE.clear();
        ChatMatcher.INSTANCE.pushUser(user1);
        assertTrue(ChatMatcher.INSTANCE.hasUserInPool(user1));
        ChatMatcher.INSTANCE.removeUser(user1);
    }

    @Test
    public void removeUserFromPool() {
        ChatMatcher.INSTANCE.clear();
        ChatMatcher.INSTANCE.pushUser(user1);
        assertTrue(ChatMatcher.INSTANCE.hasUserInPool(user1));
        ChatMatcher.INSTANCE.removeUser(user1);
        assertTrue(!ChatMatcher.INSTANCE.hasUserInPool(user1));
    }

    @Test
    public void isFirstInPool() {
        ChatMatcher.INSTANCE.clear();
        ChatMatcher.INSTANCE.pushUser(user1);
        ChatMatcher.INSTANCE.pushUser(user2);
        ChatMatcher.INSTANCE.pushUser(user3);
        ChatMatcher.INSTANCE.pushUser(user4);
        ChatMatcher.INSTANCE.pushUser(user5);
        System.out.println(ChatMatcher.INSTANCE.getFirstInPool().getUid());
        assertTrue(ChatMatcher.INSTANCE.getFirstInPool() == user1);
        ChatMatcher.INSTANCE.removeUser(user1);
        ChatMatcher.INSTANCE.removeUser(user2);
        ChatMatcher.INSTANCE.removeUser(user3);
        ChatMatcher.INSTANCE.removeUser(user4);
        ChatMatcher.INSTANCE.removeUser(user5);
    }

    @Test
    public void isInPool() {
        ChatMatcher.INSTANCE.clear();
        ChatMatcher.INSTANCE.pushUser(user1);
        ChatMatcher.INSTANCE.pushUser(user2);
        ChatMatcher.INSTANCE.pushUser(user3);
        ChatMatcher.INSTANCE.pushUser(user4);
        ChatMatcher.INSTANCE.pushUser(user5);
        assertTrue(ChatMatcher.INSTANCE.hasUserInPool(user1));
        assertTrue(ChatMatcher.INSTANCE.hasUserInPool(user2));
        assertTrue(ChatMatcher.INSTANCE.hasUserInPool(user3));
        assertTrue(ChatMatcher.INSTANCE.hasUserInPool(user4));
        assertTrue(ChatMatcher.INSTANCE.hasUserInPool(user5));
        ChatMatcher.INSTANCE.removeUser(user1);
        ChatMatcher.INSTANCE.removeUser(user2);
        ChatMatcher.INSTANCE.removeUser(user3);
        ChatMatcher.INSTANCE.removeUser(user4);
        ChatMatcher.INSTANCE.removeUser(user5);
    }

    @Test
    public void testMatch() {
        ChatMatcher.INSTANCE.clear();
        ChatMatcher.INSTANCE.pushUser(user1);
        ChatMatcher.INSTANCE.pushUser(user2);
        ChatMatcher.INSTANCE.pushUser(user3);
        Chat chat = ChatMatcher.INSTANCE.match();
        assertTrue(chat != null);
        assertTrue(chat.getFirstUser() == user1);
        assertTrue(chat.getSecondUser() == user2);
        System.out.println(ChatMatcher.INSTANCE.getFirstInPool().getUid());
        //assertTrue(ChatMatcher.INSTANCE.getFirstInPool() == user3);
        ChatMatcher.INSTANCE.removeUser(user3);

    }
}
