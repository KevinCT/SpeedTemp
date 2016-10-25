package com.zweigbergk.speedswede.core.local;

import com.zweigbergk.speedswede.core.Chat;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;

public class UserData {
    ListExtension<Chat> chats;

    public UserData() {
        chats = new ArrayListExtension<>();
    }

    public ListExtension<Chat> getChats() {
        return chats;
    }

    public Chat getChatByUid(String uid) {
        for (Chat chat : chats) {
            if (chat.getId().equals(uid))
                return chat;
        }

        return null;
    }
}
