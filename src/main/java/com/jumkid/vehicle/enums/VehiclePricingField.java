package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum VehiclePricingField {

    @FieldNameConstants.Include ID("id"),
    @FieldNameConstants.Include MSRP("msrp");

    @JsonValue
    private final String value;

    VehiclePricingField(String value) { this.value = value; }

    public String value() {
        return value;
    }

}
