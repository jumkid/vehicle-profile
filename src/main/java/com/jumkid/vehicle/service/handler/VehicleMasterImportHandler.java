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

    public Integer batchImport(InputStream is) throws IOException {
        final String COMMA_DELIMITER = ",";

        List<VehicleMasterEntity> vehicleMasterEntityList = new ArrayList<>();

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
                        .accessScope(AccessScope.valueOf(getColumn(columns, 6).toUpperCase()))
                        .vehicleEngine(VehicleEngine.builder()
                                .name(getColumn(columns, 7))
                                .type(getColumn(columns, 8))
                                .cylinder(Integer.parseInt(getColumn(columns, 9)))
                                .displacement(Float.parseFloat(getColumn(columns, 10)))
                                .fuelType(getColumn(columns, 11))
                                .horsepower(Integer.parseInt(getColumn(columns, 12)))
                                .horsepowerRpm(Integer.parseInt(getColumn(columns, 13)))
                                .torque(Integer.parseInt(getColumn(columns, 14)))
                                .torqueRpm(Integer.parseInt(getColumn(columns, 15)))
                                .manufacturerEngineCode(getColumn(columns, 16))
                                .build()
                        )
                        .vehicleTransmission(VehicleTransmission.builder()
                                .name(getColumn(columns, 17))
                                .type(getColumn(columns, 18))
                                .drivetrain(getColumn(columns, 19))
                                .availability(getColumn(columns, 20))
                                .automaticType(getColumn(columns, 21))
                                .numberOfSpeeds(Integer.parseInt(getColumn(columns, 22)))
                                .build()
                        )
                        .build();

                dtoHandler.normalizeDTO(null, newVehicle, null);
                vehicleMasterEntityList.add(vehicleMapper.dtoToEntity(newVehicle));
            }

            if (!vehicleMasterEntityList.isEmpty()) {
                vehicleMasterEntityList = vehicleMasterRepository.saveAll(vehicleMasterEntityList);

                Integer searchIndexed = vehicleSearchRepository.saveAll(vehicleSearchMapper.entitiesToSearches(vehicleMasterEntityList));
                log.debug("saved {} vehicle search indexes for bulk import", searchIndexed);
            }

            log.info("{} records are imported", vehicleMasterEntityList.size());
            return vehicleMasterEntityList.size();

        } catch (IOException ioe) {
            log.error("Failed to extra data from the import file {}", ioe.getMessage());
        } catch (Exception e) {
            log.error("Failed to process import file {}", e.getMessage());
            throw new VehicleImportException(List.of(e.getMessage()));
        }

        return 0;

    }

    private String getColumn(String[] rec, int i) { return rec[i].trim(); }

}
