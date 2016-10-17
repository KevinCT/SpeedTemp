package com.zweigbergk.speedswede.core;

import com.zweigbergk.speedswede.util.collection.Collections;

public enum SkillCategory {
    STUDENT("student"), CHATTER("chatter"), MENTOR("mentor"), UNDEFINED("undefined");

    public static final SkillCategory DEFAULT = SkillCategory.STUDENT;
    private String value;

    SkillCategory(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static SkillCategory fromString(String string) {
        return Collections.asList(values())
                .filter(category -> category.toString().equalsIgnoreCase(string))
                .getFirst();
    }
}
