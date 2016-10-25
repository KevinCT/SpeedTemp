package com.zweigbergk.speedswede.util.methodwrapper;

import com.zweigbergk.speedswede.util.collection.MapExtension;

public interface EntryAssertion<K, V> {
    boolean accepts(MapExtension.Entry<K, V> entry);
}
