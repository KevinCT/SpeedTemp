package com.zweigbergk.speedswede.core;

import android.net.Uri;

public interface User {

    String getUid();
    boolean isAnonymous();
    String getDisplayName();
    Uri getPhotoUrl();
    String getEmail();
}
