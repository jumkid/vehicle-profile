package com.jumkid.vehicle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jumkid.vehicle.enums.AccessScope;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleSearch implements Serializable {

    private String id;

    private String name;

    private String make;

    private String model;

    private Integer modelYear;

    private AccessScope accessScope;

    private String trimLevel;

    private VehicleEngineSearch vehicleEngine;

    private VehicleTransmissionSearch vehicleTransmission;

}
