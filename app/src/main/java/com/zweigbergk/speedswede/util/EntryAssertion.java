package com.zweigbergk.speedswede.util;

import java.util.Map;

public interface EntryAssertion<K, V> {
    boolean accepts(Map.Entry<K, V> entry);
}
