package com.zweigbergk.speedswede.util;

import android.content.Intent;

import com.zweigbergk.speedswede.util.Attachable;

public interface ActivityAttachable extends Attachable {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
