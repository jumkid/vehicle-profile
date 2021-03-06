package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.service.dto.Vehicle;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel="spring", uses = {VehicleEngineMapper.class, VehicleTransmissionMapper.class})
public interface VehicleMapper {

    @Mapping(target="id", source = "entity.vehicleId")
    @Mapping(target="vehicleEngine", source="entity.vehicleEngineEntity")
    @Mapping(target="vehicleTransmission", source="entity.vehicleTransmissionEntity")
    Vehicle entityToDto(VehicleMasterEntity entity);

    @Mapping(target="vehicleId", source = "dto.id")
    @Mapping(target="vehicleEngineEntity", source="dto.vehicleEngine")
    @Mapping(target="vehicleTransmissionEntity", source="dto.vehicleTransmission")
    VehicleMasterEntity dtoToEntity(Vehicle dto);

    @Mapping(target="vehicleEngineEntity", source="partialDto.vehicleEngine")
    @Mapping(target="vehicleTransmissionEntity", source="partialDto.vehicleTransmission")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(Vehicle partialDto, @MappingTarget VehicleMasterEntity updateEntity);

    List<Vehicle> entitiesToDTOs(List<VehicleMasterEntity> entities);

}
