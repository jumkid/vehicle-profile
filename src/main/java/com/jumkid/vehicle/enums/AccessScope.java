package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AccessScope {

    PUBLIC("public"),
    PRIVATE("private");

    @JsonValue
    private final String value;

    AccessScope(String value) { this.value = value; }

    public String value() {
        return value;
    }
}
