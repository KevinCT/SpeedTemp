package com.zweigbergk.speedswede.core.local;

import com.zweigbergk.speedswede.core.Chat;

import java.util.ArrayList;
import java.util.List;

public class UserData {
    List<Chat> chats;

    public UserData() {
        chats = new ArrayList<>();
    }

    public List<Chat> getChats() {
        return chats;
    }

}
