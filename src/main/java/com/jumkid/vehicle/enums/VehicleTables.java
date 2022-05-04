package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum VehicleTables {

    @FieldNameConstants.Include VEHICLE_ID("vehicle_id"),
    @FieldNameConstants.Include VEHICLE_ENGINE_ID("vehicle_engine_id"),

    @FieldNameConstants.Include NAME("name"),
    @FieldNameConstants.Include MAKE("make"),
    @FieldNameConstants.Include MODEL("model"),
    @FieldNameConstants.Include MODEL_YEAR("model_year"),
    @FieldNameConstants.Include TYPE("type"),
    @FieldNameConstants.Include CYLINDER("cylinder"),
    @FieldNameConstants.Include FUEL_TYPE("fuel_type"),
    @FieldNameConstants.Include HORSEPOWER("horsepower"),
    @FieldNameConstants.Include TORQUE("torque"),
    @FieldNameConstants.Include MANUFACTURER_ENGINE_CODE("manufacturer_engine_code"),

    @FieldNameConstants.Include CREATION_DATE("creation_date"),
    @FieldNameConstants.Include CREATED_BY("created_by"),
    @FieldNameConstants.Include MODIFICATION_DATE("modification_date"),
    @FieldNameConstants.Include MODIFIED_BY("modified_by");

    @JsonValue
    private final String value;

    VehicleTables(String value) { this.value = value; }

    public String value() {
        return value;
    }

}
