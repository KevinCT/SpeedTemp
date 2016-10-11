package com.zweigbergk.speedswede.methodwrapper;

public interface Query<E> {
     boolean matches(E object);
}
