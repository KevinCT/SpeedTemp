package com.zweigbergk.speedswede.util.collection;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

public interface CollectionExtension<E> extends java.util.Collection<E> {
    void foreach(Client<E> client);
}
