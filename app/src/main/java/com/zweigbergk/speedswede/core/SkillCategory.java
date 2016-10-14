package com.zweigbergk.speedswede.core;


public enum SkillCategory {
    STUDENT("student"), CHATTER("chatter"), MENTOR("mentor");

    private String value;

    SkillCategory(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static SkillCategory fromString(String text) {
        if (text != null) {
            for (SkillCategory skill : SkillCategory.values()) {
                if (text.equalsIgnoreCase(skill.value)) {
                    return skill;
                }
            }
        }
        return null;
    }
}
