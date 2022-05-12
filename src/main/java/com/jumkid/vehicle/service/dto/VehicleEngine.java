package com.jumkid.vehicle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = {"vehicleEngineId"}, callSuper = false)
@Builder
public class VehicleEngine {

    private Long vehicleEngineId;

    private String name;

    private String type;

    private Integer cylinder;

    private String fuelType;

    private Integer horsepower;

    private Integer torque;

    private String manufacturerEngineCode;

}
