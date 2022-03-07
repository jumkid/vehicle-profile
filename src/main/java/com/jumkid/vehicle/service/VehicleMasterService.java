package com.jumkid.vehicle.service;

import com.jumkid.vehicle.service.dto.Vehicle;

import java.util.List;

public interface VehicleMasterService {

    List<Vehicle> getUserVehicles();

    Vehicle saveUserVehicle(Vehicle vehicle);

    void deleteUserVehicle(String vehicleId);

}
