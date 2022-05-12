package com.jumkid.vehicle;

import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleEngine;

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
                .modificationDate(LocalDateTime.now())
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
                .build();
    }
}
