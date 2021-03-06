package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum VehicleField {

    @FieldNameConstants.Include VEHICLE_ID("vehicle_id"),

    @FieldNameConstants.Include VEHICLE_ENGINE_ID("vehicle_engine_id"),
    @FieldNameConstants.Include VEHICLE_TRANSMISSION_ID("vehicle_transmission_id"),

    @FieldNameConstants.Include NAME("name"),
    @FieldNameConstants.Include MAKE("make"),
    @FieldNameConstants.Include MODEL("model"),

    @FieldNameConstants.Include ACCESS_SCOPE("access_scope"),
    @FieldNameConstants.Include ACCESSSCOPE("accessScope"),

    @FieldNameConstants.Include TRIM_LEVEL("trim_level"),
    @FieldNameConstants.Include TRIMLEVEL("trimLevel"),

    @FieldNameConstants.Include MODEL_YEAR("model_year"),
    @FieldNameConstants.Include MODELYEAR("modelYear"),

    @FieldNameConstants.Include MEDIA_GALLERY_ID("media_gallery_id"),
    @FieldNameConstants.Include MEDIAGALLERYID("mediaGalleryId"),

    @FieldNameConstants.Include CREATION_DATE("creation_date"),
    @FieldNameConstants.Include CREATED_BY("created_by"),
    @FieldNameConstants.Include MODIFICATION_DATE("modification_date"),
    @FieldNameConstants.Include MODIFIED_BY("modified_by");

    @JsonValue
    private final String value;

    VehicleField(String value) { this.value = value; }

    public String value() {
        return value;
    }

}
