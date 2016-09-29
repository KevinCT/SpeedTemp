package com.zweigbergk.speedswede.util;

public interface Query<E> {
     boolean matches(E object);
}
