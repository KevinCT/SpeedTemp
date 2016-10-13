package com.zweigbergk.speedswede.core;

/**
 * Created by FEngelbrektsson on 12/10/16.
 */

public enum SkillCategory {
    PUPIL("pupil"), UNSPECIFIED("unspecified"), MENTOR("mentor");

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
