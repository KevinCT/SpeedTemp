package com.zweigbergk.speedswede.mock;

import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.HashSetExtension;
import com.zweigbergk.speedswede.util.collection.SetExtension;

public class SnapshotMock implements ISnapshot {
    private String path;
    private Object value;

    private SetExtension<ISnapshot> children;

    public SnapshotMock(String startPath) {
        this.path = startPath;
        this.children = new HashSetExtension<>();
    }

    public SnapshotMock() {
        this("");
    }

    public void setValue(Object value) {
        if (value == null) {
            children.foreach(snapshot -> snapshot.setValue(null));
            children.clear();
        }

        this.value = value;
    }

    @Override
    public Iterable<ISnapshot> getChildren() {
        return children;
    }

    @Override
    public int getChildrenCount() {
        return children.filter(snapshot -> snapshot.getValue() != null).size();
    }

    @Override
    public boolean exists() {
        return getValue() != null;
    }

    public ISnapshot child(String path) {
        if (path.isEmpty()) {
            return this;
        }

        String firstPath = Arrays.asList(path.split("/")).getFirst();
        String restOfPath = path.substring(firstPath.length());
        ISnapshot snapshot = getChildByKey(firstPath);
        if (snapshot != null) {
            if (!restOfPath.isEmpty()) {
                return snapshot.child(restOfPath);
            }
        } else {
            ISnapshot newSnapshot = new SnapshotMock(path);
            children.add(newSnapshot);
            return newSnapshot.child(restOfPath);
        }

        return snapshot;
    }

    private ISnapshot getChildByKey(String key) {
        return children.filter(snapshot -> snapshot.getKey().equals(key)).getFirst();
    }

    @Override
    public Object getValue() {
        if (children.size() > 0) {
            return children.filter(snapshot -> snapshot.getValue() != null).size() > 0 ? children : null;
        }

        return value;
    }

    @Override
    public String getKey() {
        return Arrays.asList(path.split("/")).getLast();
    }
}
