package com.zweigbergk.speedswede;

/** Type that can be "attached to" another class with a lifecycle, to handle certain
 * responsibilities of said class. */
public interface Attachable {
    void onStart();
    void onStop();
}
