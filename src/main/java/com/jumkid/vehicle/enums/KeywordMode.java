package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum KeywordMode {

    KEYWORD("KEYWORD"),
    QUERYSTRING("QUERYSTRING");

    @JsonValue
    private final String value;

    KeywordMode(String value) { this.value = value; }

    public String value() {
        return value;
    }

}
