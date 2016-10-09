package com.zweigbergk.speedswede.util;

public interface Client<Content> {
    void supply(Content content);

    interface Interest<Content> {
        boolean caresFor(Content content);
    }
}
