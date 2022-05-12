package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.service.dto.Vehicle;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel="spring")
public interface VehicleSearchMapper {

    @Mapping(target="vehicleEngineSearch", source="entity.vehicleEngineEntity")
    @Mapping(target="id", source = "entity.vehicleId")
    VehicleSearch entityToSearchMeta(VehicleMasterEntity entity);

    @Mapping(target="vehicleEngineSearch", source="dto.vehicleEngine")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    VehicleSearch dtoToSearch(Vehicle dto);
}
