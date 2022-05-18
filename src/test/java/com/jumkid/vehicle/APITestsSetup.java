package com.jumkid.vehicle;

import com.jumkid.vehicle.enums.AccessScope;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleEngine;
import com.jumkid.vehicle.service.dto.VehicleTransmission;

import java.time.LocalDateTime;

public class APITestsSetup {

    static String DUMMY_ID = "xxxx-xxxx";

    static Vehicle buildVehicle() {
        LocalDateTime now = LocalDateTime.now();
        return Vehicle.builder()
                .id(DUMMY_ID)
                .name("test vehicle")
                .make("toyota")
                .model("highlander")
                .modelYear(2022)
                .accessScope(AccessScope.PRIVATE)
                .modificationDate(LocalDateTime.now())
                .createdBy("test")
                .vehicleEngine(VehicleEngine.builder()
                                .name("test vehicle engine")
                                .type("V-Engine")
                                .cylinder(4)
                                .fuelType("diesel")
                                .horsepower(180)
                                .torque(140)
                                .manufacturerEngineCode("ABC")
                                .build()
                )
                .vehicleTransmission(VehicleTransmission.builder()
                        .name("test vehicle transmission")
                        .type("automatic")
                        .automaticType("AMT")
                        .availability("standard")
                        .numberOfSpeeds(6)
                        .build()
                )
                .build();
    }
}
