package com.jumkid.vehicle.service.mapper;

import com.jumkid.vehicle.model.VehiclePricingEntity;
import com.jumkid.vehicle.service.dto.VehiclePricing;
import org.mapstruct.*;

@Mapper(componentModel="spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface VehiclePricingMapper {

    VehiclePricing entityToDTO(VehiclePricingEntity entity);

    VehiclePricingEntity dtoToEntity(VehiclePricing dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VehiclePricing partialDto, @MappingTarget VehiclePricingEntity updateEntity);
}
