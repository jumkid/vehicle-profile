package com.jumkid.vehicle.repository;

import com.jumkid.share.security.AccessScope;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.exception.VehicleImportException;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.service.dto.VehicleFieldValuePair;

import java.util.List;

public interface VehicleSearchRepository {

    PagingResults<VehicleSearch> searchByUser(String keyword, Integer size, Integer page, String userId)
            throws VehicleSearchException;

    PagingResults<VehicleSearch> searchByAccessScope(String keyword, Integer size, Integer page, AccessScope accessScope)
            throws VehicleSearchException;

    PagingResults<VehicleSearch> searchByMatchFields(Integer size, Integer page,
                                                     List<VehicleFieldValuePair<String>> matchFields,
                                                     AccessScope accessScope) throws VehicleSearchException;

    List<String> searchForAggregation(VehicleField aggFieldName, List<VehicleFieldValuePair<String>> matchFields, Integer size)
            throws VehicleSearchException;

    VehicleSearch save(VehicleSearch vehicleSearch);

    Integer saveAll(List<? extends VehicleSearch> vehicleSearchList) throws VehicleImportException;

    Integer update(String vehicleId, VehicleSearch partialVehicleSearch);

    void delete(String vehicleId);

}
