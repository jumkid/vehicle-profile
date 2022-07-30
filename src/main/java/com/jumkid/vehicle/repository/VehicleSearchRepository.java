package com.jumkid.vehicle.repository;

import com.jumkid.vehicle.model.VehicleSearch;

import java.util.List;

public interface VehicleSearchRepository {

    VehicleSearch save(VehicleSearch vehicleSearch);

    Integer saveAll(List<VehicleSearch> vehicleSearchList);

    Integer update(String vehicleId, VehicleSearch partialVehicleSearch);

    void delete(String vehicleId);

}
