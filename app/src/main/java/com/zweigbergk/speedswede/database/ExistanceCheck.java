package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Executable;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.ProductLock;

public class ExistanceCheck {

    private ProductBuilder<Boolean> mBuilder;

    private static final ProductBuilder.Blueprint<Boolean> existanceBlueprint = items ->
            ((Boolean)items.get(ProductLock.ASSERTION));

    private ExistanceCheck(DatabaseReference ref) {
        mBuilder = new ProductBuilder<>(existanceBlueprint);
        mBuilder.attachLocks(ProductLock.ASSERTION);

        ref.addListenerForSingleValueEvent(getListener());
    }

    static ExistanceCheck ifExists(DatabaseReference ref) {
        return new ExistanceCheck(ref);
    }

    static  ExistanceCheck ifExists(Chat chat) {
        DatabaseReference ref = FirebaseDatabase.getInstance().
                getReference().child(Constants.CHATS).child(chat.getId());

        return new ExistanceCheck(ref);
    }

    static  ExistanceCheck ifExists(User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().
                getReference().child(Constants.USERS).child(user.getUid());

        return new ExistanceCheck(ref);
    }

    public void then(Executable executable) {
        mBuilder.addExecutable(executable);
    }

    private ValueEventListener getListener() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBuilder.addItem(ProductLock.ASSERTION, dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }
}
