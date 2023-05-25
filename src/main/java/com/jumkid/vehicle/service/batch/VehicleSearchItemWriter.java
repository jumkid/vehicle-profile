package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class VehicleSearchItemWriter implements ItemWriter<VehicleSearch> {

    private final VehicleSearchRepository vehicleSearchRepository;

    public VehicleSearchItemWriter(VehicleSearchRepository vehicleSearchRepository) {
        this.vehicleSearchRepository = vehicleSearchRepository;
    }

    @Override
    public void write(Chunk<? extends VehicleSearch> chunk) throws Exception {
        vehicleSearchRepository.saveAll(chunk.getItems());
    }
}
