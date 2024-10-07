package org.example.homeandgarden.entity.enums;

public enum DeliveryMethod {
    COURIER_DELIVERY ("Courier Delivery"),
    CUSTOMER_PICKUP ("Customer Pickup");

    private String value;

    DeliveryMethod(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
