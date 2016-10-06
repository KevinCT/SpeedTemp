package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DbChatHandler;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.ProductLock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatListListener implements ValueEventListener {

    public static final String TAG = ChatListListener.class.getSimpleName().toUpperCase();

    private Set<Client<List<Chat>>> mClients;

    public ChatListListener(Collection<Client<List<Chat>>> clients) {
        mClients = new HashSet<>(clients);
    }

    public void addClient(Client<List<Chat>> client) {
        mClients.add(client);
    }

    public void removeClient(Client<List<Chat>> client) {
        mClients.remove(client);
    }

    public void notifyClients(DatabaseEvent event, List<Chat> list) {
        Lists.forEach(mClients, client -> client.supply(list));
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        List<Chat> result = new ArrayList<>();

        ProductBuilder<List<Chat>> pb = new ProductBuilder<>(
                items -> (List<Chat>) items.get(ProductLock.CHAT_LIST),
                ProductLock.CHAT_LIST);


//        mRoot.child(CHATS).orderByChild(FIRST_USER)
//                .equalTo(DbUserHandler.INSTANCE.getActiveUserId())
//                .addValueEventListener(new ValueEventListener() {

        Log.d(TAG, "onDataChange");


        long amount = dataSnapshot.getChildrenCount();

        Log.d(TAG, "Amount: " + amount);

        pb.requireState(ProductLock.CHAT_LIST, list -> ((List) list).size() == amount);

        pb.addItem(ProductLock.CHAT_LIST, result);

        pb.addClient(product -> {
            notifyClients(DatabaseEvent.ADDED, product);
            Log.d(TAG, "add product");
            Log.d(TAG, "list " + product.toString());
        });

        for (DataSnapshot child : dataSnapshot.getChildren()) {
            Log.d(TAG, "Looping trough childs");
            Log.d(TAG, "Child: " + child.getRef().toString());

            DbChatHandler.INSTANCE.convertToChat(child, chat -> {
                Log.d(TAG, "Adding to list: " + chat.toString());
                result.add(chat);
                pb.updateState();
            });
        }
        Log.d(TAG, "list size: " + result.size() + "");
        Log.d(TAG, "dataSnapshot size: " + dataSnapshot.getChildrenCount());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
