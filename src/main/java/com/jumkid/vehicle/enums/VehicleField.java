package com.jumkid.vehicle.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants(onlyExplicitlyIncluded = true)
public enum VehicleField {

    @FieldNameConstants.Include ID("id"),
    @FieldNameConstants.Include VEHICLE_ID("vehicle_id"),

    @FieldNameConstants.Include VEHICLE_ENGINE_ID("vehicle_engine_id"),
    @FieldNameConstants.Include VEHICLE_TRANSMISSION_ID("vehicle_transmission_id"),
    @FieldNameConstants.Include VEHICLE_PRICING_ID("vehicle_pricing_id"),

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

    @FieldNameConstants.Include CATEGORY("category"),

    @FieldNameConstants.Include VEHICLEEINGINE_NAME("vehicleEngine.name"),
    @FieldNameConstants.Include VEHICLEEINGINE_CYLINDER("vehicleEngine.cylinder"),

    @FieldNameConstants.Include VEHICLETRANSMISSSION_NAME("vehicleTransmission.name"),
    @FieldNameConstants.Include VEHICLETRANSMISSSION_DRIVETRAIN("vehicleTransmission.drivetrain"),

    @FieldNameConstants.Include CREATED_ON("created_on"),
    @FieldNameConstants.Include CREATEDON("createdOn"),

    @FieldNameConstants.Include CREATED_BY("created_by"),
    @FieldNameConstants.Include CREATEDBY("createdBy"),

    @FieldNameConstants.Include MODIFIED_ON("modified_on"),
    @FieldNameConstants.Include MODIFIEDON("modifiedOn"),
    @FieldNameConstants.Include MODIFIED_BY("modified_by"),
    @FieldNameConstants.Include MODIFIEDBY("modifiedBy");

    @JsonValue
    private final String value;

    VehicleField(String value) { this.value = value; }

    public String value() {
        return value;
    }

}
