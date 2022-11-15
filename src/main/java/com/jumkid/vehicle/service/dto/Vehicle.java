package com.jumkid.vehicle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jumkid.share.security.AccessScope;
import com.jumkid.share.service.dto.GenericDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = {"id"}, callSuper = false)
public class Vehicle extends GenericDTO {

    private String id;

    private String name;

    @NotBlank(message = "Vehicle make is required")
    private String make;

    @NotBlank(message = "Vehicle model is required")
    private String model;

    @Min(1800)
    private Integer modelYear;

    @NotNull(message = "Access scope is required")
    private AccessScope accessScope;

    private String trimLevel;

    private String mediaGalleryId;

    private String category;

    @Valid
    private VehicleEngine vehicleEngine;

    @Valid
    private VehicleTransmission vehicleTransmission;

    /**
     * This constructor is for lombok builder only since it is subclass of generic DTO
     *
     */
    @Builder
    public Vehicle(String id, String name, String make, String model, Integer modelYear, String trimLevel,
                   AccessScope accessScope, String mediaGalleryId, String category,
                   String createdBy, LocalDateTime creationDate, String modifiedBy, LocalDateTime modificationDate,
                   VehicleEngine vehicleEngine, VehicleTransmission vehicleTransmission) {

        super(createdBy, creationDate, modifiedBy, modificationDate);

        this.id = id;
        this.name = name;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
        this.accessScope = accessScope;
        this.trimLevel = trimLevel;
        this.mediaGalleryId = mediaGalleryId;
        this.category = category;

        this.vehicleEngine = vehicleEngine;
        this.vehicleTransmission = vehicleTransmission;
    }

}