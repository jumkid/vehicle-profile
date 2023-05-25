package com.jumkid.vehicle.service.handler;

import com.jumkid.share.security.AccessScope;
import com.jumkid.vehicle.exception.VehicleImportException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleEngine;
import com.jumkid.vehicle.service.dto.VehicleTransmission;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.validation.constraints.NotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class VehicleMasterImportHandler {

    private final DTOHandler dtoHandler;

    private final VehicleMasterRepository vehicleMasterRepository;

    private final VehicleSearchRepository vehicleSearchRepository;

    private final VehicleMapper vehicleMapper;
    private final VehicleSearchMapper vehicleSearchMapper;

    public VehicleMasterImportHandler(DTOHandler dtoHandler,
                                      VehicleMasterRepository vehicleMasterRepository,
                                      VehicleSearchRepository vehicleSearchRepository, VehicleMapper vehicleMapper, VehicleSearchMapper vehicleSearchMapper) {
        this.dtoHandler = dtoHandler;
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.vehicleSearchRepository = vehicleSearchRepository;
        this.vehicleMapper = vehicleMapper;
        this.vehicleSearchMapper = vehicleSearchMapper;
    }

    public Integer batchImport(InputStream is) throws VehicleImportException {
        final String COMMA_DELIMITER = ",";

        List<Vehicle> vehicleList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isBlank()) continue;

                String[] columns = line.split(COMMA_DELIMITER);
                Vehicle newVehicle = Vehicle.builder()
                        .name(getColumn(columns, 0))
                        .make(getColumn(columns, 1))
                        .model(getColumn(columns, 2))
                        .modelYear(Integer.parseInt(getColumn(columns, 3)))
                        .trimLevel(getColumn(columns, 4))
                        .mediaGalleryId(getColumn(columns, 5))
                        .category(getColumn(columns, 6))
                        .accessScope(AccessScope.valueOf(getColumn(columns, 7).toUpperCase()))
                        .vehicleEngine(VehicleEngine.builder()
                                .name(getColumn(columns, 8))
                                .type(getColumn(columns, 9))
                                .cylinder(Integer.parseInt(getColumn(columns, 10)))
                                .displacement(Float.parseFloat(getColumn(columns, 11)))
                                .fuelType(getColumn(columns, 12))
                                .horsepower(Integer.parseInt(getColumn(columns, 13)))
                                .horsepowerRpm(Integer.parseInt(getColumn(columns, 14)))
                                .torque(Integer.parseInt(getColumn(columns, 15)))
                                .torqueRpm(Integer.parseInt(getColumn(columns, 16)))
                                .manufacturerEngineCode(getColumn(columns, 17))
                                .build()
                        )
                        .vehicleTransmission(VehicleTransmission.builder()
                                .name(getColumn(columns, 18))
                                .type(getColumn(columns, 19))
                                .drivetrain(getColumn(columns, 20))
                                .availability(getColumn(columns, 21))
                                .automaticType(getColumn(columns, 22))
                                .numberOfSpeeds(Integer.parseInt(getColumn(columns, 23)))
                                .build()
                        )
                        .build();

                dtoHandler.normalizeDTO(null, newVehicle, null);
                vehicleList.add(newVehicle);
            }

            return batchImport(vehicleList);
        } catch (IOException ioe) {
            log.error("Failed to extra data from the import file {}", ioe.getMessage());
            throw new VehicleImportException(List.of(ioe.getMessage()));
        }
    }

    public Integer batchImport(@NotNull List<Vehicle> vehicleList) throws VehicleImportException {
        if (vehicleList.isEmpty()) return 0;

        try {
            List<VehicleMasterEntity> entities = vehicleMapper.dtosToEntities(vehicleList);
            entities = vehicleMasterRepository.saveAll(entities);

            Integer searchIndexed = vehicleSearchRepository.saveAll(vehicleSearchMapper.entitiesToSearches(entities));
                log.debug("saved {} vehicle search indexes for bulk import", searchIndexed);

            log.info("{} records are imported", entities.size());
            return entities.size();
        } catch (Exception e) {
            log.error("Failed to process import file {}", e.getMessage());
            throw new VehicleImportException(List.of(e.getMessage()));
        }

    }

    private String getColumn(String[] rec, int i) { return rec[i].trim(); }

}
