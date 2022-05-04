package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleEngineEntity;
import com.jumkid.vehicle.service.dto.VehicleEngine;
import org.mapstruct.Mapper;

@Mapper(componentModel="spring")
public interface VehicleEngineMapper {

    VehicleEngine entityToDTO( VehicleEngineEntity entity);

    VehicleEngineEntity dtoToEntity( VehicleEngine dto);

}
