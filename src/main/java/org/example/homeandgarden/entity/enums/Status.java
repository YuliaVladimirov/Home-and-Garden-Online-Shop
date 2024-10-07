package org.example.homeandgarden.entity.enums;

public enum Status {
    CREATED ("Created"),
    PENDING_PAYMENT ("Pending Payment"),
    PAID ("Paid"),
    ON_THE_WAY ("On the Way"),
    DELIVERED ("Delivered"),
    CANCELED ("Canceled");

    private String value;

    Status(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    }
