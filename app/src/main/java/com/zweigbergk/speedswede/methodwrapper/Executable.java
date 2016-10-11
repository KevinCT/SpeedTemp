package com.zweigbergk.speedswede.methodwrapper;

public interface Executable {
    void run();

    interface Interest<Content> {
        boolean caresFor(Content p);
    }
}
