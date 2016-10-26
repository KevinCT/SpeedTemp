package com.zweigbergk.speedswede.mock;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.util.collection.Arrays;

public class SnapshotExtension implements ISnapshot {

    private final DataSnapshot snapshot;

    public SnapshotExtension(DataSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public ISnapshot child(String path) {
        return new SnapshotExtension(snapshot.child(path));
    }

    @Override
    public Object getValue() {
        return snapshot.getValue();
    }

    @Override
    public String getKey() {
        return snapshot.getKey();
    }

    @Override
    public void setValue(Object value) {
        throw new RuntimeException(
                "SnapshotExtension does not support setValue().It is only used for mocking.");
    }

    @Override
    public Iterable<ISnapshot> getChildren() {
        return Arrays.asList(snapshot.getChildren()).map(SnapshotExtension::new);
    }

    @Override
    public int getChildrenCount() {
        return 0;
    }

    @Override
    public boolean exists() {
        return snapshot.exists();
    }
}
