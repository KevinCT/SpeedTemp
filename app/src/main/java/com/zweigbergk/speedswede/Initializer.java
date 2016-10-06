package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.DatabaseNode;
import com.zweigbergk.speedswede.database.DbChatHandler;
import com.zweigbergk.speedswede.database.DbUserHandler;

public class Initializer {

    public static void onLogin() {
        DbChatHandler.INSTANCE.initialize();
        DbUserHandler.INSTANCE.initialize();

        DatabaseHandler.getInstance().registerListener(DatabaseNode.CHATS);
    }
}
