package com.jumkid.vehicle.model;

import com.jumkid.vehicle.enums.AccessScope;
import com.jumkid.vehicle.enums.VehicleEngineField;
import com.jumkid.vehicle.enums.VehicleTransmissionField;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

import com.jumkid.vehicle.enums.VehicleField;

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
    @Column(name = VehicleField.Fields.VEHICLE_ID, updatable = false, nullable = false)
    private String vehicleId;

    @Column(name = VehicleField.Fields.NAME)
    private String name;

    @Column(name = VehicleField.Fields.MAKE)
    private String make;

    @Column(name = VehicleField.Fields.MODEL)
    private String model;

    @Column(name = VehicleField.Fields.MODEL_YEAR)
    private Integer modelYear;

    @Enumerated(EnumType.STRING)
    @Column(name = VehicleField.Fields.ACCESS_SCOPE)
    private AccessScope accessScope;

    @Column(name = VehicleField.Fields.TRIM_LEVEL)
    private String trimLevel;

    @Column(name = VehicleField.Fields.CREATION_DATE)
    private LocalDateTime creationDate;

    @Column(name = VehicleField.Fields.CREATED_BY)
    private String createdBy;

    @Column(name = VehicleField.Fields.MODIFICATION_DATE)
    private LocalDateTime modificationDate;

    @Column(name = VehicleField.Fields.MODIFIED_BY)
    private String modifiedBy;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = VehicleField.Fields.VEHICLE_ENGINE_ID,
            referencedColumnName = VehicleEngineField.Fields.ID)
    private VehicleEngineEntity vehicleEngineEntity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = VehicleField.Fields.VEHICLE_TRANSMISSION_ID,
            referencedColumnName = VehicleTransmissionField.Fields.ID)
    private VehicleTransmissionEntity vehicleTransmissionEntity;
}
