package com.jumkid.vehicle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Builder
public class VehicleEngine {

    private Long id;

    private String name;

    private String type;

    private Integer cylinder;

    private Float displacement;

    private String fuelType;

    private Integer horsepower;
    private Integer horsepowerRpm;

    private Integer torque;
    private Integer torqueRpm;

    private String manufacturerEngineCode;

}
