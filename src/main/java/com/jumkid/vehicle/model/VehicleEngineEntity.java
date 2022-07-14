package com.jumkid.vehicle.model;

import com.jumkid.vehicle.enums.VehicleEngineField;
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
    @Column(name = VehicleEngineField.Fields.ID)
    private String id;

    @Column(name = VehicleEngineField.Fields.NAME)
    private String name;

    @Column(name = VehicleEngineField.Fields.TYPE)
    private String type;

    @Column(name = VehicleEngineField.Fields.CYLINDER)
    private Integer cylinder;

    @Column(name = VehicleEngineField.Fields.DISPLACEMENT)
    private Float displacement;

    @Column(name = VehicleEngineField.Fields.FUEL_TYPE)
    private String fuelType;

    @Column(name = VehicleEngineField.Fields.HORSEPOWER)
    private Integer horsepower;

    @Column(name = VehicleEngineField.Fields.HORSEPOWER_RPM)
    private Integer horsepowerRpm;

    @Column(name = VehicleEngineField.Fields.TORQUE)
    private Integer torque;

    @Column(name = VehicleEngineField.Fields.TORQUE_RPM)
    private Integer torqueRpm;

    @Column(name = VehicleEngineField.Fields.MANUFACTURER_ENGINE_CODE)
    private String manufacturerEngineCode;
}
