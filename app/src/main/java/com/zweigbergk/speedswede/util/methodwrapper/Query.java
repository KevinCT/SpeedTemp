package com.zweigbergk.speedswede.util.methodwrapper;

public interface Query<E> {
     boolean matches(E object);
}
