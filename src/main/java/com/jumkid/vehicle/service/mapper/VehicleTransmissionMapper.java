package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehicleTransmissionEntity;
import com.jumkid.vehicle.service.dto.VehicleTransmission;
import org.mapstruct.*;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehicleTransmissionMapper {

    VehicleTransmission entityToDTO(VehicleTransmissionEntity entity);

    VehicleTransmissionEntity dtoToEntity( VehicleTransmission dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VehicleTransmission partialDto, @MappingTarget VehicleTransmissionEntity updateEntity);
}
