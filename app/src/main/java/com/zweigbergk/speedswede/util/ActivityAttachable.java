package com.zweigbergk.speedswede.util;

import android.content.Intent;


public interface ActivityAttachable {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
