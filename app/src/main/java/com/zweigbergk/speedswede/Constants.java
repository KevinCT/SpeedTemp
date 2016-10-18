package com.zweigbergk.speedswede;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.PreferenceWrapper;
import com.zweigbergk.speedswede.util.collection.Collections;
import com.zweigbergk.speedswede.util.collection.List;

import java.util.Arrays;
import java.util.Random;

public class Constants {
    public static final String ERROR = "ERROR";
    public static final String DEBUG = "DEBUG";

    //Database nodes
    public static final String CHATS = "chats";
    public static final String USERS = "users";
    public static final String MESSAGES = "messages";
    public static final String TIMESTAMP = "timeStamp";
    public static final String FIRST_USER = "firstUser";
    public static final String POOL = "pool";
    public static final String SECOND_USER = "secondUser";
    public static final String NAME = "name";
    public static final String UNDEFINED = "undefined";

    //User Attributes
    public static final String USER_ID = "uid";
    public static final String DISPLAY_NAME = "displayName";
    public static final String PREFERENCES = "preferences";
    public static final String NOTIFICATIONS = "receiveNotifications";
    public static final String LANGUAGE = "language";

    public static final String LIKED_BY_FIRST_USER = "likedByFirstUser";
    public static final String LIKED_BY_SECOND_USER = "likedBySecondUser";

    public static final String FIRST_LOGIN = "firstLogin";
    public static final String TIME_IN_QUEUE = "timeInQueue";
    public static final String SKILL_CATEGORY = "skillCategory";

    public static final String BANS = "bans";
    public static final String BANLIST = "banList";

    public static final String SETTINGS_FIRST_SETUP = "settings_first_setup";

    //Topics
    public static final int DEAFULT_TOPIC_IMAGE = R.drawable.ic_default_chat;

    public enum Topic {
        Cars(R.drawable.ic_taxi), Theatre(R.drawable.ic_comedy), Universe(R.drawable.ic_nature),
        School(R.drawable.ic_scholarship), Basketball(R.drawable.ic_basketball), Pets(R.drawable.ic_animals),
        Clothes(R.drawable.ic_shirt), Movies(R.drawable.ic_movie), Football(R.drawable.ic_soccer),
        Travel(R.drawable.ic_travel), Music(R.drawable.ic_music), Food(R.drawable.ic_food), Books(R.drawable.ic_book),
        Fitness(R.drawable.ic_fitness);

        private final int resourceId;

        Topic(int resourceId) {
            this.resourceId = resourceId;
        }

        public int getResourceId() {
            return resourceId;
        }

        public static Topic getRandom() {
            Random random = new Random();
            random.nextInt();
            random.nextInt();
            Topic[] values = values();

            return values[random.nextInt(values.length)];
        }

        public static Topic fromString(String name) {
            final Topic returnTopic = Collections.asList(values())
                    .filter(topic -> topic.name().equalsIgnoreCase(name))
                    .getFirst();
            return returnTopic;
        }
    }

    //Chat list
    public static final int MAX_PREVIEW_LENGTH = 40;

    //Languages
    public static final String ENGLISH = "en";
    public static final String SWEDISH = "sv";
    public static final String ARABIC = "ar";
    public static final String TURKISH = "tr";
    public static final String DARI = "da";

    //For saving instance state
    public static final String CHAT_PARCEL = "chatParcel";

    public static String makePath(String... constants) {
        if (constants.length == 0)
            return "";

        StringBuilder builder = new StringBuilder();
        Lists.forEach(Arrays.asList(constants), c -> {
            builder.append(c);
            builder.append('/');
        });

        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public static String preference(String preference) {
        return makePath(PREFERENCES, preference);
    }

    public static List<PreferenceWrapper> shells =
            Collections.asList(
                    PreferenceWrapper.StringWrapper.shell(),
                    PreferenceWrapper.BooleanWrapper.shell(),
                    PreferenceWrapper.LongWrapper.shell());
}