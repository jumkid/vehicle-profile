package com.jumkid.vehicle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Builder
public class VehicleTransmission {

    private Long id;

    private String name;

    private String type;

    private String availability;

    private String automaticType;

    private Integer numberOfSpeeds;

    private String drivetrain;

}
