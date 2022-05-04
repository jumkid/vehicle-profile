package com.jumkid.vehicle.repository;

import com.jumkid.vehicle.model.VehicleSearch;

public interface VehicleSearchRepository {

    VehicleSearch save(VehicleSearch vehicleSearch);

    void delete(String docId);

}
