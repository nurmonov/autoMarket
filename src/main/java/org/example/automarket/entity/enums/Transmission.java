package org.example.automarket.entity.enums;


public enum Transmission {
    AVTOMAT, MEXANIKA, CVT, ROBOTIC;

    public static Transmission from(String value) {
        for (Transmission t : values()) {
            if (t.name().equalsIgnoreCase(value)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Invalid transmission: " + value);
    }
}
