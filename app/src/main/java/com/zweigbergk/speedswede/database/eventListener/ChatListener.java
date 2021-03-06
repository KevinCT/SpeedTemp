package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.HashSetExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;
import com.zweigbergk.speedswede.util.collection.SetExtension;
import com.zweigbergk.speedswede.util.factory.ChatFactory;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.Locale;

public class ChatListener implements ChildEventListener {
    private static final String TAG = ChatListener.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    private static final String CLIENT_FOR_ALL_CHATS = "key_to_listen_to_every_chat";

    private MapExtension<String, SetExtension<Client<DataChange<Chat>>>> chatClients;


    public ChatListener() {
        super();

        chatClients = new HashMapExtension<>();
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.d(TAG, "Snapshot onChildAdded: " + dataSnapshot.toString());
        ChatFactory.deserializeChat(dataSnapshot).then(this::notifyAdded);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        ChatFactory.deserializeChat(dataSnapshot).then(this::notifyChanged);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        ChatFactory.deserializeChat(dataSnapshot).then(this::notifyRemoved);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(Constants.ERROR, databaseError.getMessage());
    }


    private void notifyClients(DatabaseEvent event, Chat chat) {
        String id = chat.getId();

        DataChange<Chat> dataChange;
        switch(event) {
            case ADDED:
                dataChange = DataChange.added(chat);
                break;
            case REMOVED:
                dataChange = DataChange.removed(chat);
                break;
            case CHANGED:
                dataChange = DataChange.modified(chat);
                break;
            case INTERRUPTED: default:
                dataChange = DataChange.cancelled(chat);
                break;
        }

        chatClients.filter(clientEntry -> {
                    String entryID = clientEntry.getKey();
                    return entryID.equals(id) || entryID.equals(CLIENT_FOR_ALL_CHATS);
                })
                .values()
                .foreach(list -> list.foreach(client -> client.supply(dataChange)));
    }

    private void notifyAdded(Chat chat) {
        notifyClients(DatabaseEvent.ADDED, chat);
    }

    private void notifyRemoved(Chat chat) {
        notifyClients(DatabaseEvent.REMOVED, chat);
    }

    private void notifyChanged(Chat chat) {
        notifyClients(DatabaseEvent.CHANGED, chat);
    }


     /**
      * Adds a client that will receive updates whenever the chat is added/removed/changed.
      * Should only be called from overriding methods in this class.
      * */
    private void addClient(String chatId, Client<DataChange<Chat>> client) {
        if (!chatClients.containsKey(chatId)) {
            chatClients.put(chatId, new HashSetExtension<>());
        }

        chatClients.get(chatId).add(client);
    }

    /**
     * Adds a client that will receive updates whenever the chat is added/removed/changed.
     * */
    @SuppressWarnings("unused")
    public void addClient(Chat chat, Client<DataChange<Chat>> client) {
        addClient(chat.getId(), client);
    }

    /**
     * Adds a client that will receive updates whenever <u>any</u> chat is added/removed/changed.
     * */
    public void addClient(Client<DataChange<Chat>> client) {
        addClient(CLIENT_FOR_ALL_CHATS, client);
    }


    /**
     * Stops a client from receiving updates from the particular chat.
     * */
    private void removeClient(String chatId, Client<DataChange<Chat>> client) {
        if (!chatClients.containsKey(chatId)) {
            chatClients.put(chatId, new HashSetExtension<>());
        }

        chatClients.get(chatId).remove(client);
    }


    /**
     * Stops a client from receiving updates from the particular chat.
     * */
    @SuppressWarnings("unused")
    public void removeClient(Chat chat, Client<DataChange<Chat>> client) {
        removeClient(chat.getId(), client);
    }

    /**
     * Removes a client from the set of clients that will receive updates whenever
     * <u>any</u> chat is added/removed/changed.
     * */
    public void removeClient(Client<DataChange<Chat>> client) {
        removeClient(CLIENT_FOR_ALL_CHATS, client);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && this.getClass() == other.getClass() && hashCode() == other.hashCode();
    }

    /**
     * Overriding hashCode since ChatListener is used in hash maps.
     */
    @Override
    public int hashCode() {
        return chatClients.hashCode();
    }
}
