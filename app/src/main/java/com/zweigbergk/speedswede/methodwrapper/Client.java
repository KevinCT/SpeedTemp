package com.zweigbergk.speedswede.methodwrapper;

public interface Client<Content> {
    void supply(Content content);

    interface Interest<Content> {
        boolean caresFor(Content content);
    }
}
