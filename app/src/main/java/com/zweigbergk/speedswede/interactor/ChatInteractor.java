package com.zweigbergk.speedswede.interactor;

import java.util.Random;

public class ChatInteractor {

    public String getRandomChatName() {
        Random r = new Random();
        int i = r.nextInt(10);
        return "chat_name_" + i;
    }
}
