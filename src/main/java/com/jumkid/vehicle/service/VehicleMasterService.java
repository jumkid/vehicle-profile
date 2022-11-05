package com.jumkid.vehicle.service;

import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleFieldValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface VehicleMasterService {

    List<Vehicle> getUserVehicles();

    PagingResults<Vehicle> searchUserVehicles(final String keyword, final Integer size, final Integer page)
            throws VehicleSearchException;

    PagingResults<Vehicle> searchPublicVehicles(final String keyword, final Integer size, final Integer page)
            throws VehicleSearchException;

    PagingResults<Vehicle> searchByCriteria(final String keyword,
                                                   final Integer size,
                                                   final Integer page,
                                                   final SearchByCriteria searchFunction) throws VehicleSearchException;

    PagingResults<Vehicle> searchByMatchFields(final Integer size,
                                               final Integer page,
                                               final List<VehicleFieldValuePair<String>> matchFields)
            throws VehicleSearchException;

    List<String> searchForAggregation(final VehicleField field,
                                      final List<VehicleFieldValuePair<String>> matchFields,
                                      final Integer size)
            throws VehicleSearchException;

    Vehicle getUserVehicle(String vehicleId) throws VehicleNotFoundException;

    Vehicle saveUserVehicle(Vehicle vehicle);

    Vehicle updateUserVehicle(String vehicleId, Vehicle vehicle);

    void deleteUserVehicle(String vehicleId);

    Integer importVehicleMaster(InputStream is) throws IOException;

}
