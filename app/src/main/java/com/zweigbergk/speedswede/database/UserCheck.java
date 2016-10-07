package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.ProductLock;

public class UserCheck {

    public enum Type { NORMAL, POOL }

    private ProductBuilder<User> mBuilder;

    protected UserCheck() {
        mBuilder = new ProductBuilder<>(items -> {
           String name = (String) items.get(ProductLock.NAME);
            String uid = (String) items.get(ProductLock.ID);

            return new UserProfile(name, uid);
        });
        mBuilder.attachLocks(ProductLock.NAME, ProductLock.ID);
    }

    private Client<User> mClient;

    protected static UserCheck ifExists(final User user, DatabaseReference userRef) {
        final UserCheck result = new UserCheck();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    result.mBuilder.addItem(ProductLock.NAME, user.getDisplayName());
                    result.mBuilder.addItem(ProductLock.ID, user.getUid());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return result;
    }

    static UserCheck ifExists(final String userId, DatabaseReference userRef) {
        final UserCheck result = new UserCheck();

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    result.mBuilder.addItem(ProductLock.NAME, dataSnapshot.child(Constants.DISPLAY_NAME).getValue());
                    result.mBuilder.addItem(ProductLock.ID, userId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return result;
    }



    private void setBuilder(ProductBuilder<User> builder) {
        mBuilder = builder;
        mBuilder.addClient(mClient);
    }

    public void then(Client<User> client) {
        mBuilder.addClient(client);
    }

    public void sendUserTo(Client<User> client) {
        mBuilder.addClient(client);
    }
}
