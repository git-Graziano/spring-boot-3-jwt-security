package com.alibou.security.dto;

public enum AuthorityEnum {
    ADMIN("Admin"),
    USER("User");

    public final String label;

    private AuthorityEnum(String label) {
        this.label = label;
    }

    public static AuthorityEnum valueOfLabel(String label) {
        for (AuthorityEnum e : values()) {
            if (e.label.equals(label)) {
                return e;
            }
        }
        return null;
    }
}
