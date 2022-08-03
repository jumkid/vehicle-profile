package com.jumkid.vehicle.repository;

import com.jumkid.vehicle.exception.VehicleImportException;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleSearch;

import java.util.List;

public interface VehicleSearchRepository {

    List<VehicleSearch> search(String keyword, Integer size, String userId) throws VehicleSearchException;

    VehicleSearch save(VehicleSearch vehicleSearch);

    Integer saveAll(List<VehicleSearch> vehicleSearchList) throws VehicleImportException;

    Integer update(String vehicleId, VehicleSearch partialVehicleSearch);

    void delete(String vehicleId);

}
