package com.jumkid.vehicle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jumkid.vehicle.enums.VehicleField;
import lombok.*;

import jakarta.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = {"field"}, callSuper = false)
@Builder
public class VehicleFieldValuePair<T> {

    @NotNull
    private VehicleField field;

    @NotNull
    private T value;

}
