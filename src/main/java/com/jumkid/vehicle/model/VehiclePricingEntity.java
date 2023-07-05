package com.jumkid.vehicle.model;

import com.jumkid.vehicle.enums.VehicleEngineField;
import com.jumkid.vehicle.enums.VehiclePricingField;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehicle_pricing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiclePricingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = VehicleEngineField.Fields.ID)
    private String id;

    @Column(name = VehiclePricingField.Fields.MSRP)
    private Float msrp;
}
