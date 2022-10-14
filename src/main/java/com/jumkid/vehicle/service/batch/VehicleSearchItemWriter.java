package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.lang.NonNull;

import java.util.List;

public class VehicleSearchItemWriter implements ItemWriter<VehicleSearch> {

    private final VehicleSearchRepository vehicleSearchRepository;

    public VehicleSearchItemWriter(VehicleSearchRepository vehicleSearchRepository) {
        this.vehicleSearchRepository = vehicleSearchRepository;
    }

    @Override
    public void write(@NonNull List vehicleSearchList) throws Exception {
        vehicleSearchRepository.saveAll(vehicleSearchList);
    }

}
