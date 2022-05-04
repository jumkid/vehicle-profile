package com.jumkid.vehicle.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.jumkid.vehicle.enums.VehicleTables;

@Entity
@Table(name = "vehicle_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleMasterEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = VehicleTables.Fields.VEHICLE_ID, updatable = false, nullable = false)
    private String vehicleId;

    @Column(name = VehicleTables.Fields.NAME)
    private String name;

    @Column(name = VehicleTables.Fields.MAKE)
    private String make;

    @Column(name = VehicleTables.Fields.MODEL)
    private String model;

    @Column(name = VehicleTables.Fields.MODEL_YEAR)
    private Integer modelYear;

    @Column(name = VehicleTables.Fields.CREATION_DATE)
    private LocalDateTime creationDate;

    @Column(name = VehicleTables.Fields.CREATED_BY)
    private String createdBy;

    @Column(name = VehicleTables.Fields.MODIFICATION_DATE)
    private LocalDateTime modificationDate;

    @Column(name = VehicleTables.Fields.MODIFIED_BY)
    private String modifiedBy;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = VehicleTables.Fields.VEHICLE_ID)
    private VehicleEngineEntity vehicleEngineEntity;

}
