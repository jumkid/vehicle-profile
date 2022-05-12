package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum VehicleEngineField {
    @FieldNameConstants.Include ID("id"),
    @FieldNameConstants.Include NAME("name"),
    @FieldNameConstants.Include TYPE("type"),
    @FieldNameConstants.Include CYLINDER("cylinder"),

    @FieldNameConstants.Include FUEL_TYPE("fuel_type"),
    @FieldNameConstants.Include FUELTYPE("fuelType"),

    @FieldNameConstants.Include HORSEPOWER("horsepower"),
    @FieldNameConstants.Include TORQUE("torque"),
    @FieldNameConstants.Include MANUFACTURER_ENGINE_CODE("manufacturer_engine_code"),
    @FieldNameConstants.Include MANUFACTURERENGINECODE("manufacturerEngineCode");

    @JsonValue
    private final String value;

    VehicleEngineField(String value) { this.value = value; }

    public String value() {
        return value;
    }

}
