package com.jumkid.vehicle.model;

import com.jumkid.vehicle.enums.VehicleTables;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "vehicle_engine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEngineEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = VehicleTables.Fields.VEHICLE_ENGINE_ID)
    private String id;

    @Column(name = VehicleTables.Fields.NAME)
    private String name;

    @Column(name = VehicleTables.Fields.TYPE)
    private String type;

    @Column(name = VehicleTables.Fields.CYLINDER)
    private Integer cylinder;

    @Column(name = VehicleTables.Fields.FUEL_TYPE)
    private String fuelType;

    @Column(name = VehicleTables.Fields.HORSEPOWER)
    private Integer horsepower;

    @Column(name = VehicleTables.Fields.TORQUE)
    private Integer torque;

    @Column(name = VehicleTables.Fields.MANUFACTURER_ENGINE_CODE)
    private String manufacturerEngineCode;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = VehicleTables.Fields.VEHICLE_ID)
    private VehicleMasterEntity vehicleMasterEntity;
}
