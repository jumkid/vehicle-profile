package com.jumkid.vehicle.model;

import com.jumkid.share.model.GenericEntity;
import com.jumkid.share.security.AccessScope;
import com.jumkid.vehicle.enums.VehicleEngineField;
import com.jumkid.vehicle.enums.VehiclePricingField;
import com.jumkid.vehicle.enums.VehicleTransmissionField;
import lombok.*;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.*;

import com.jumkid.vehicle.enums.VehicleField;

@Table(name = "vehicle_master")
@Entity
@SuperBuilder @Data @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(of = {"id"}, callSuper = true)
public class VehicleMasterEntity extends GenericEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = VehicleField.Fields.VEHICLE_ID, updatable = false, nullable = false)
    private String id;

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

    @Column(name = VehicleField.Fields.MEDIA_GALLERY_ID)
    private String mediaGalleryId;

    @Column(name = VehicleField.Fields.CATEGORY)
    private String category;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = VehicleField.Fields.VEHICLE_ENGINE_ID,
            referencedColumnName = VehicleEngineField.Fields.ID)
    private VehicleEngineEntity vehicleEngineEntity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = VehicleField.Fields.VEHICLE_TRANSMISSION_ID,
            referencedColumnName = VehicleTransmissionField.Fields.ID)
    private VehicleTransmissionEntity vehicleTransmissionEntity;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = VehicleField.Fields.VEHICLE_PRICING_ID,
            referencedColumnName = VehiclePricingField.Fields.ID)
    private VehiclePricingEntity vehiclePricingEntity;
}
