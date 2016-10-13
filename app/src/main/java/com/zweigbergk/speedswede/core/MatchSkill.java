package com.zweigbergk.speedswede.core;

/**
 * Created by FEngelbrektsson on 12/10/16.
 */

public enum MatchSkill {
    LEARNER("learner"), CHATTER("chatter"), MENTOR("mentor");

    private String value;

    MatchSkill(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
