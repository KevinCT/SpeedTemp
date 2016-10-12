package com.zweigbergk.speedswede.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.util.Statement;

public abstract class DbHandler {
    void delete(DatabaseReference ref) {
        ref.removeValue();
    }

    Statement hasReference(DatabaseReference ref) {
        Statement statement = new Statement();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                statement.setReturnValue(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                statement.setBuildFailed(true);
            }
        });

        return statement;
    }
}
