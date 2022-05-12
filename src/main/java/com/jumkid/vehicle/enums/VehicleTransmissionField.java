package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum VehicleTransmissionField {

    @FieldNameConstants.Include ID("id"),
    @FieldNameConstants.Include NAME("name"),
    @FieldNameConstants.Include AVAILABILITY("availability"),

    @FieldNameConstants.Include AUTOMATIC_TYPE("automatic_type"),
    @FieldNameConstants.Include AUTOMATICTYPE("automaticType"),

    @FieldNameConstants.Include TYPE("type"),
    @FieldNameConstants.Include NUMBER_OF_SPEEDS("number_of_speeds"),
    @FieldNameConstants.Include NUMBEROFSPEEDS("numberOfSpeeds");

    @JsonValue
    private final String value;

    VehicleTransmissionField(String value) { this.value = value; }

    public String value() {
        return value;
    }
}
