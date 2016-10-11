package com.zweigbergk.speedswede.database.eventListener;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.methodwrapper.Client;

public class DataQuery implements ValueEventListener {

    private Client<DataSnapshot> mClient;

    public DataQuery(Client<DataSnapshot> client) {
        mClient = client;
    }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mClient.supply(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
}
