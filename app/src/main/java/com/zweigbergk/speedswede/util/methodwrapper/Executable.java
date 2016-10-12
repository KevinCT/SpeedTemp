package com.zweigbergk.speedswede.util.methodwrapper;

public interface Executable {
    void run();

    interface Interest<Content> {
        boolean caresFor(Content p);
    }
}
