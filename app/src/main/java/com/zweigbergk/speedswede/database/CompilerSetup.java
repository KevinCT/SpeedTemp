package com.zweigbergk.speedswede.database;


import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.util.Client;

import java.util.List;

public class CompilerSetup {

    ChatListCompiler compiler;

    public CompilerSetup() {

    }

    public CompilerSetup get(ChatListCompiler compiler) {
        this.compiler = compiler;

        return this;
    }

    public void sendTo(Client<List<Chat>> client) {
        compiler.setClient(client);
    }
}
