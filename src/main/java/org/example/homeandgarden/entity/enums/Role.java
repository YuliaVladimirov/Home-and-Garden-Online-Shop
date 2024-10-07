package org.example.homeandgarden.entity.enums;

public enum Role {
    CLIENT ("Client"),
    ADMINISTRATOR ("Administrator");

    private String value;

    Role(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }

}
