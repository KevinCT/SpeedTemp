package com.zweigbergk.speedswede.util.methodwrapper;

import com.zweigbergk.speedswede.util.collection.Map;

public interface EntryAssertion<K, V> {
    boolean accepts(Map.Entry<K, V> entry);
}
