package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class VehicleProfileReindexProcessor implements ItemProcessor<VehicleMasterEntity, VehicleSearch>  {

    private final VehicleSearchMapper vehicleSearchMapper;

    public VehicleProfileReindexProcessor(VehicleSearchMapper vehicleSearchMapper) {
        this.vehicleSearchMapper = vehicleSearchMapper;
    }

    @Override
    @Nullable
    public VehicleSearch process(@NonNull VehicleMasterEntity vehicleMasterEntity) {
        return vehicleSearchMapper.entityToSearchMeta(vehicleMasterEntity);
    }

}
