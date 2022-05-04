package com.jumkid.vehicle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleEngineSearch {

    private String name;

    private String type;

    private Integer cylinder;

    private String fuelType;

    private Integer horsepower;

    private Integer torque;

    private String manufacturerEngineCode;

}
