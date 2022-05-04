package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel="spring")
public interface VehicleSearchMapper {

    @Mapping(target="vehicleEngineSearch", source="entity.vehicleEngineEntity")
    @Mapping(target="id", source = "entity.vehicleId")
    VehicleSearch entityToSearchMeta(VehicleMasterEntity entity);

}
