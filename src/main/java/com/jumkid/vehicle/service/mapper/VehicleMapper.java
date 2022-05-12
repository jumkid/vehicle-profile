package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.service.dto.Vehicle;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel="spring", uses = VehicleEngineMapper.class)
public interface VehicleMapper {

    @Mapping(target="id", source = "entity.vehicleId")
    @Mapping(target="vehicleEngine", source="entity.vehicleEngineEntity")
    Vehicle entityToDto(VehicleMasterEntity entity);

    @Mapping(target="vehicleId", source = "dto.id")
    @Mapping(target="vehicleEngineEntity", source="dto.vehicleEngine")
    VehicleMasterEntity dtoToEntity(Vehicle dto);

    @Mapping(target="vehicleEngineEntity", source="partialDto.vehicleEngine")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateMasterEntityFromDto(Vehicle partialDto, @MappingTarget VehicleMasterEntity updateEntity);

    List<Vehicle> entitiesToDTOs(List<VehicleMasterEntity> entities);

}
