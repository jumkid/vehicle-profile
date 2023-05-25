package com.jumkid.vehicle;

import com.jumkid.share.security.AccessScope;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleEngine;
import com.jumkid.vehicle.service.dto.VehicleTransmission;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component("apiTestSetup")
public class APITestSetup {

    static String DUMMY_ID = "xxxx-xxxx";

    public Vehicle buildVehicle() {
        return buildVehicle(DUMMY_ID);
    }

    public Vehicle buildVehicle(String id) {
        return Vehicle.builder()
                .id(id)
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

    public List<Vehicle> buildVehicles() {
        List<Vehicle> list = new ArrayList<>();
        for (int i=0; i<10; i++) {
            list.add(buildVehicle(DUMMY_ID + "-" + i));
        }
        return list;
    }

}
