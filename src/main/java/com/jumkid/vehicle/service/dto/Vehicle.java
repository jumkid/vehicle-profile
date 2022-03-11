package com.jumkid.vehicle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jumkid.share.controller.dto.GenericDTO;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
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

    /**
     * This constructor is for lombok builder only since it is subclass of generic DTO
     *
     */
    @Builder
    public Vehicle(String id, String name, String make, String model, Integer modelYear,
            String createdBy, LocalDateTime creationDate, String modifiedBy, LocalDateTime modificationDate) {

        super(createdBy, creationDate, modifiedBy, modificationDate);

        this.id = id;
        this.name = name;
        this.make = make;
        this.model = model;
        this.modelYear = modelYear;
    }

}