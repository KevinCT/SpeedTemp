package com.zweigbergk.speedswede.util;

import java.util.Collection;

public class Lists {
    public static <E> void forEach(Collection<E> collection, Client<E> client) {
        for (E element : collection)
            client.supply(element);
    }
}
