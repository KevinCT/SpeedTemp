package com.zweigbergk.speedswede.util;

public interface Executable {
    void run();

    interface Interest<Content> {
        boolean caresFor(Content p);
    }
}
