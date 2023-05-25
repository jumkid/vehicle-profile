package com.jumkid.vehicle.model;

import com.jumkid.vehicle.enums.VehicleTransmissionField;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicle_transmission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleTransmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = VehicleTransmissionField.Fields.ID)
    private String id;

    @Column(name = VehicleTransmissionField.Fields.NAME)
    private String name;

    @Column(name = VehicleTransmissionField.Fields.TYPE)
    private String type;

    @Column(name = VehicleTransmissionField.Fields.DRIVETRAIN)
    private String drivetrain;

    @Column(name = VehicleTransmissionField.Fields.AVAILABILITY)
    private String availability;

    @Column(name = VehicleTransmissionField.Fields.AUTOMATIC_TYPE)
    private String automaticType;

    @Column(name = VehicleTransmissionField.Fields.NUMBER_OF_SPEEDS)
    private Integer numberOfSpeeds;
}
