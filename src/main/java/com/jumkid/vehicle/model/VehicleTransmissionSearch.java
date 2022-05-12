package com.jumkid.vehicle.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jumkid.vehicle.enums.VehicleTransmissionField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleTransmissionSearch implements Serializable {

    private String name;

    private String type;

    private String availability;

    private String automaticType;

    private Integer numberOfSpeeds;
}
