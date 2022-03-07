package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.service.dto.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel="spring")
public interface VehicleMapper {

    @Mapping(source = "entity.vehicleId", target="id")
    Vehicle entityToDto(VehicleMasterEntity entity);

    @Mapping(target="vehicleId", source = "dto.id")
    VehicleMasterEntity dtoToEntity(Vehicle dto);

    List<Vehicle> entitiesToDTOs(List<VehicleMasterEntity> entities);

}
