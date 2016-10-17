package com.zweigbergk.speedswede.core.local;

import com.zweigbergk.speedswede.core.Chat;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;

public class UserData {
    List<Chat> chats;

    public UserData() {
        chats = new ArrayList<>();
    }

    public List<Chat> getChats() {
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
