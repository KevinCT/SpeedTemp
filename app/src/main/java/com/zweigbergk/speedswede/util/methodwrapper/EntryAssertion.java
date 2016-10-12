package com.zweigbergk.speedswede.util.methodwrapper;

import java.util.Map;

public interface EntryAssertion<K, V> {
    boolean accepts(Map.Entry<K, V> entry);
}
