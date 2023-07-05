package com.jumkid.vehicle.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jumkid.share.security.AccessScope;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import static com.jumkid.share.util.Constants.YYYYMMDDTHHMMSS3S;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleSearch implements Serializable {

    private String id;

    private String name;

    private String make;

    private String model;

    private Integer modelYear;

    private AccessScope accessScope;

    private String trimLevel;

    private String category;

    private VehicleEngineSearch vehicleEngine;

    private VehicleTransmissionSearch vehicleTransmission;

    private VehiclePricingSearch vehiclePricing;

    private String createdBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = YYYYMMDDTHHMMSS3S)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime creationDate;

}
