package com.jumkid.vehicle.service;

import com.jumkid.vehicle.service.dto.Vehicle;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface VehicleMasterService {

    List<Vehicle> getUserVehicles();

    Vehicle saveUserVehicle(Vehicle vehicle);

    Vehicle saveUserVehicle(String vehicleId, Vehicle vehicle);

    void deleteUserVehicle(String vehicleId);

    Integer importVehicleMaster(InputStream is) throws IOException;

}
