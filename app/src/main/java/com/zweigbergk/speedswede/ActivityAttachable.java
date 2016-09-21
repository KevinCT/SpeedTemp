package com.zweigbergk.speedswede;

import android.content.Intent;

public interface ActivityAttachable extends Attachable {
    void onActivityResult(int requestCode, int resultCode, Intent data);
}
