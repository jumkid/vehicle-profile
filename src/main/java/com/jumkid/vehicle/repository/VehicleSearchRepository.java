package com.jumkid.vehicle.repository;

import com.jumkid.vehicle.model.VehicleSearch;

public interface VehicleSearchRepository {

    VehicleSearch save(VehicleSearch vehicleSearch);

    VehicleSearch update(String vehicleId, VehicleSearch partialVehicleSearch);

    void delete(String vehicleId);

}
