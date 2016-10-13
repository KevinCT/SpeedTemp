package com.zweigbergk.speedswede.core;

/**
 * Created by FEngelbrektsson on 12/10/16.
 */

public enum MatchSkill {
    BEGINNER("beginner"), INTERMEDIATE("intermediate"), SKILLED("skilled");

    private String value;

    MatchSkill(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
