package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;

import static com.zweigbergk.speedswede.Constants.CHATS;

public class ChatExistanceCheck  {

    private ProductBuilder<Chat> mBuilder;

    private Client<Chat> mClient;

    static ChatExistanceCheck ifExists(String chatId) {
        ChatExistanceCheck result = new ChatExistanceCheck();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference().child(CHATS).child(chatId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    result.setBuilder(ChatFactory.serializeChat(dataSnapshot));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return result;
    }

    private void setBuilder(ProductBuilder<Chat> builder) {
        mBuilder = builder;
        mBuilder.addClient(mClient);
    }

    public void then(Client<Chat> client) {
        mClient = client;
    }

    public void sendChatTo(Client<Chat> client) {
        mClient = client;
    }
}
