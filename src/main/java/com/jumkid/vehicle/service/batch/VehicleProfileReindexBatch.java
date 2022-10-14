package com.jumkid.vehicle.service.batch;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@EnableBatchProcessing
public class VehicleProfileReindexBatch {

    public final VehicleMasterRepository vehicleMasterRepository;

    private final VehicleSearchMapper vehicleSearchMapper;

    private final VehicleSearchRepository vehicleSearchRepository;

    public VehicleProfileReindexBatch(VehicleMasterRepository vehicleMasterRepository,
                                      VehicleSearchMapper vehicleSearchMapper,
                                      VehicleSearchRepository vehicleSearchRepository) {
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.vehicleSearchMapper = vehicleSearchMapper;
        this.vehicleSearchRepository = vehicleSearchRepository;
    }

    @Bean
    public RepositoryItemReader<VehicleMasterEntity> reader() {
        RepositoryItemReader<VehicleMasterEntity> reader = new RepositoryItemReader<>();

        reader.setRepository(vehicleMasterRepository);
        reader.setMethodName("findAll");
        reader.setPageSize(100);

        Map<String, Sort.Direction> sorts = new HashMap<>();
        sorts.put("id", Sort.Direction.ASC);
        reader.setSort(sorts);

        return reader;
    }

    @Bean
    public ItemWriter<VehicleSearch> writer() {
        return new VehicleSearchItemWriter(vehicleSearchRepository);
    }

    @Bean
    public ItemProcessor<VehicleMasterEntity, VehicleSearch> processor() {
        return new VehicleProfileReindexProcessor(vehicleSearchMapper);
    }

}
