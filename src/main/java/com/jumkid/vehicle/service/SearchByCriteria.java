package com.jumkid.vehicle.service;

import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleSearch;

@FunctionalInterface
public interface SearchByCriteria {
    PagingResults<VehicleSearch> search(final String keyword, final Integer size, final Integer page)
            throws VehicleSearchException;
}
