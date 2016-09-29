package com.zweigbergk.speedswede.util;

/** Type that can be "attached to" another class with a lifecycle, to handle certain
 * responsibilities of said class. */
public interface Attachable {
    void onStart();

    void onStop();
}
