package com.zweigbergk.speedswede.core;

import java.util.Arrays;

public interface User {
    String getUid();
    String getDisplayName();
    Object getPreference(Preference preference);


    enum Preference {
        NOTIFICATIONS, LANGUAGE, SWEDISH_SKILL, STRANGER_SWEDISH_SKILL;


        private static final Preference[] booleans = new Preference[] { NOTIFICATIONS };
        private static final Preference[] strings = new Preference[] { LANGUAGE };
        private static final Preference[] longs = new Preference[] { SWEDISH_SKILL, STRANGER_SWEDISH_SKILL };

        public boolean accepts(boolean value) {
            return Arrays.asList(booleans).contains(this);
        }

        public boolean accepts(String value) {
            return Arrays.asList(strings).contains(this);
        }

        public boolean accepts(long value) {
            return Arrays.asList(longs).contains(this);
        }
    }
}
