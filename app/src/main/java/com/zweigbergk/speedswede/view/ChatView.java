package com.zweigbergk.speedswede.view;

import android.content.Context;

import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;

public interface ChatView {
    <T> T contextualize(ProviderMethod<T, Context> method);
}
