package com.Turfbooking.models.enums;

public enum Turfs {
    TURF01("turf01"),
    TURF02("turf02"),
    TURF03("turf03");

    private String value;

    Turfs(String value)  {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
