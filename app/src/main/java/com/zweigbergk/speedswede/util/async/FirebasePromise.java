package com.zweigbergk.speedswede.util.async;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.zweigbergk.speedswede.util.async.PromiseNeed.SNAPSHOT;

public class FirebasePromise extends Promise<DataSnapshot> {

    public FirebasePromise(DatabaseReference reference) {
        super(null);

        setResultForm(items -> items.getSnapshot(PromiseNeed.SNAPSHOT));
        requires(SNAPSHOT);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                addItem(SNAPSHOT, dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                setPromiseFailed(true);
            }
        });
    }
}
