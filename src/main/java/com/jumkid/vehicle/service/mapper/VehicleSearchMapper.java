package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.service.dto.Vehicle;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel="spring")
public interface VehicleSearchMapper {

    @Mapping(target="vehicleEngine", source="entity.vehicleEngineEntity")
    @Mapping(target="vehicleTransmission", source="entity.vehicleTransmissionEntity")
    @Mapping(target="id", source = "entity.id")
    VehicleSearch entityToSearchMeta(VehicleMasterEntity entity);

    @Mapping(target="vehicleEngine", source="dto.vehicleEngine")
    @Mapping(target="vehicleTransmission", source="dto.vehicleTransmission")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    VehicleSearch dtoToSearch(Vehicle dto);

    List<VehicleSearch> entitiesToSearches(List<VehicleMasterEntity> entityList);

    List<VehicleSearch> dtoListToSearches(List<Vehicle> dtoList);
}
