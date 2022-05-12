package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleEngineEntity;
import com.jumkid.vehicle.service.dto.VehicleEngine;
import org.mapstruct.*;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleEngineMapper {

    @Mapping(target="vehicleEngineId", source = "entity.id")
    VehicleEngine entityToDTO( VehicleEngineEntity entity);

    VehicleEngineEntity dtoToEntity( VehicleEngine dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VehicleEngine partialDto, @MappingTarget VehicleEngineEntity updateEntity);

}
