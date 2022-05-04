package com.jumkid.vehicle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleSearch {

    private String id;

    private String make;

    private String model;

    private Integer modelYear;

    private VehicleEngineSearch vehicleEngineSearch;

}
