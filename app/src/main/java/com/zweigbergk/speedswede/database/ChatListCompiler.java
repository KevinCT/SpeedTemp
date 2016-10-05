package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.List;

public class ChatListCompiler {

    public static final String TAG = ChatListCompiler.class.getSimpleName().toUpperCase();

    private List<Chat> list = new ArrayList<>();
    private Client<List<Chat>> client = list -> Log.d("AsyncChatList", "NO CLIENT RECEIVED!");

    private int entries = 0;

    public void setClient(Client<List<Chat>> client) {
        if (list.size() == entries) {
            client.supply(list);
        } else {
            this.client = client;
        }
    }

    void run(DataSnapshot dataSnapshot) {
        Log.d(TAG, "get() going strong...");
        for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
            ++entries;
            DbChatHandler.INSTANCE.convertToChatById(idSnapshot.getKey(), this::addToList);
        }
    }

    private void addToList(Chat chat) {
        list.add(chat);
        Log.d(TAG, String.format("We have %d out of %d chats!", list.size(), entries));

        if (list.size() == entries) {
            client.supply(list);
        }
    }
}
