package com.jumkid.vehicle.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@Builder
public class VehiclePricing {

    private Long id;

    private Float msrp;
}
