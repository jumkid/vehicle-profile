package com.jumkid.vehicle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleTransmissionSearch implements Serializable {

    private String name;

    private String type;

    private String drivetrain;

    private String availability;

    private String automaticType;

    private Integer numberOfSpeeds;
}
