package com.jumkid.vehicle.service;

import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.exception.ModificationDatetimeNotFoundException;
import com.jumkid.vehicle.exception.VehicleDataOutdatedException;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class VehicleMasterServiceImpl implements VehicleMasterService{

    private final VehicleMasterRepository vehicleMasterRepository;

    private final VehicleSearchRepository vehicleSearchRepository;

    private final UserProfileManager userProfileManager;

    private final VehicleMapper vehicleMapper;
    private final VehicleSearchMapper vehicleSearchMapper;

    @Autowired
    public VehicleMasterServiceImpl(VehicleMasterRepository vehicleMasterRepository,
                                    VehicleSearchRepository vehicleSearchRepository,
                                    UserProfileManager userProfileManager, VehicleMapper vehicleMapper,
                                    VehicleSearchMapper vehicleSearchMapper) {
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.vehicleSearchRepository = vehicleSearchRepository;
        this.userProfileManager = userProfileManager;
        this.vehicleMapper = vehicleMapper;
        this.vehicleSearchMapper = vehicleSearchMapper;
    }

    @Override
    public List<Vehicle> getUserVehicles() {
        UserProfile userProfile = getUserProfile();
        String userId = userProfile.getId();

        List<VehicleMasterEntity> vehicleMasterEntities = vehicleMasterRepository.findByCreatedBy(userId);
        log.debug("Found {} domain data records for user {}", vehicleMasterEntities.size(), userId);

        return vehicleMapper.entitiesToDTOs(vehicleMasterEntities);
    }

    @Override
    @PostAuthorize("@vehicleAccessPermissionAuthorizer.hasViewPermission(returnObject)")
    public Vehicle getUserVehicle(String vehicleId) throws VehicleNotFoundException {
        VehicleMasterEntity entity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });
        return vehicleMapper.entityToDto(entity);
    }

    @Override
    @Transactional
    public Vehicle saveUserVehicle(Vehicle vehicle) throws UserProfileNotFoundException{
        normalizeDTO(null, vehicle, null);

        VehicleMasterEntity entity = vehicleMapper.dtoToEntity(vehicle);

        entity = vehicleMasterRepository.save(entity);
        log.debug("new user vehicle data saved");

        vehicleSearchRepository.save(vehicleSearchMapper.entityToSearchMeta(entity));

        return vehicleMapper.entityToDto(entity);
    }

    @Override
    @Transactional
    @PreAuthorize("@vehicleAccessPermissionAuthorizer.hasUpdatePermission(#vehicleId)")
    public Vehicle updateUserVehicle(String vehicleId, Vehicle vehicle) {
        VehicleMasterEntity updateEntity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });

        normalizeDTO(vehicleId, vehicle, updateEntity);

        vehicleMapper.updateEntityFromDto(vehicle, updateEntity);

        updateEntity = vehicleMasterRepository.save(updateEntity);
        log.debug("updated user vehicle master data");

        vehicleSearchRepository.update(vehicleId, vehicleSearchMapper.dtoToSearch(vehicle));
        log.debug("updated user vehicle search indexer");

        return vehicleMapper.entityToDto(updateEntity);
    }

    @Override
    @Transactional
    @PreAuthorize("@vehicleAccessPermissionAuthorizer.hasUpdatePermission(#vehicleId)")
    public void deleteUserVehicle(String vehicleId) {
        VehicleMasterEntity existingEntity = vehicleMasterRepository.getById(vehicleId);

        try {
            vehicleMasterRepository.delete(existingEntity);
            log.debug("Vehicle with id {} is removed.", vehicleId);

            vehicleSearchRepository.delete(vehicleId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to delete user vehicle by id {}", vehicleId);
        }

    }

    @Override
    public Integer importVehicleMaster(InputStream is) throws IOException {
        log.info("importing vehicle master data");

        final String COMMA_DELIMITER = ",";
        List<VehicleMasterEntity> vehicleMasterEntityList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isBlank()) continue;

                String[] domainStr = line.split(COMMA_DELIMITER);
                Vehicle newVehicle = Vehicle.builder()
                            .name(domainStr[0].trim())
                            .make(domainStr[1].trim())
                            .model(domainStr[2].trim())
                            .modelYear(Integer.parseInt(domainStr[3].trim()))
                            .build();
                normalizeDTO(newVehicle);
                vehicleMasterEntityList.add(vehicleMapper.dtoToEntity(newVehicle));
            }

            if (!vehicleMasterEntityList.isEmpty()) {
                vehicleMasterEntityList = vehicleMasterRepository.saveAll(vehicleMasterEntityList);
            }
            log.info("{} records are imported", vehicleMasterEntityList.size());
            return vehicleMasterEntityList.size();
        } catch (IOException e) {
            log.error("Unable to extra data from the import file {}", e.getMessage());
        }
        return 0;
    }

    private void normalizeDTO(Vehicle dto) { normalizeDTO(null, dto, null);}

    private void normalizeDTO(String vehicleId, Vehicle dto, VehicleMasterEntity oldEntity) {
        if (vehicleId != null) { dto.setId(vehicleId); }

        LocalDateTime now = LocalDateTime.now();
        UserProfile userProfile = getUserProfile();
        String userId = userProfile.getId();

        if (oldEntity != null) {
            dto.setCreatedBy(oldEntity.getCreatedBy());
            dto.setCreationDate(oldEntity.getCreationDate());

            if (dto.getModificationDate() == null) { throw new ModificationDatetimeNotFoundException(); }

            if (!oldEntity.getModificationDate().truncatedTo(ChronoUnit.MILLIS).equals(dto.getModificationDate())) {
                throw new VehicleDataOutdatedException();
            }
        } else {
            dto.setCreationDate(now);
            dto.setCreatedBy(userId);
        }

        dto.setModifiedBy(userId);
        dto.setModificationDate(now);
    }

    private UserProfile getUserProfile() {
        UserProfile userProfile = userProfileManager.fetchUserProfile();
        if (userProfile == null) {
            throw new UserProfileNotFoundException("User profile could not be found. Access is denied");
        } else {
            return userProfile;
        }
    }

}
