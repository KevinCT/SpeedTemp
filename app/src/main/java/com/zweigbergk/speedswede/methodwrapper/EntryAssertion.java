package com.zweigbergk.speedswede.methodwrapper;

import java.util.Map;

public interface EntryAssertion<K, V> {
    boolean accepts(Map.Entry<K, V> entry);
}
