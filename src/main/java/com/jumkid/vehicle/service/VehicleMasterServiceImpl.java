package com.jumkid.vehicle.service;

import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.repository.VehicleSearchRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.handler.DTOHandler;
import com.jumkid.vehicle.service.handler.VehicleMasterImportHandler;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import com.jumkid.vehicle.service.mapper.VehicleSearchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
public class VehicleMasterServiceImpl implements VehicleMasterService{

    private final VehicleMasterRepository vehicleMasterRepository;

    private final VehicleSearchRepository vehicleSearchRepository;

    private final UserProfileManager userProfileManager;

    private final VehicleMasterImportHandler vehicleMasterImportHandler;
    private final DTOHandler dtoHandler;

    private final VehicleMapper vehicleMapper;
    private final VehicleSearchMapper vehicleSearchMapper;

    @Autowired
    public VehicleMasterServiceImpl(VehicleMasterRepository vehicleMasterRepository,
                                    VehicleSearchRepository vehicleSearchRepository,
                                    UserProfileManager userProfileManager, VehicleMasterImportHandler vehicleMasterImportHandler, DTOHandler dtoHandler, VehicleMapper vehicleMapper,
                                    VehicleSearchMapper vehicleSearchMapper) {
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.vehicleSearchRepository = vehicleSearchRepository;
        this.userProfileManager = userProfileManager;
        this.vehicleMasterImportHandler = vehicleMasterImportHandler;
        this.dtoHandler = dtoHandler;
        this.vehicleMapper = vehicleMapper;
        this.vehicleSearchMapper = vehicleSearchMapper;
    }

    @Override
    public List<Vehicle> getUserVehicles() {
        UserProfile userProfile = userProfileManager.fetchUserProfile();
        String userId = userProfile.getId();

        List<VehicleMasterEntity> vehicleMasterEntities = vehicleMasterRepository.findByCreatedBy(userId);
        log.info("Found {} vehicles for user {}", vehicleMasterEntities.size(), userId);

        return vehicleMapper.entitiesToDTOs(vehicleMasterEntities);
    }

    @Override
    @PostAuthorize("@vehicleAccessPermissionAuthorizer.hasViewPermission(returnObject)")
    public Vehicle getUserVehicle(String vehicleId) throws VehicleNotFoundException {
        VehicleMasterEntity entity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });
        log.info("Found vehicle with id {} records for user", vehicleId);

        return vehicleMapper.entityToDto(entity);
    }

    @Override
    @Transactional
    public Vehicle saveUserVehicle(Vehicle vehicle) throws UserProfileNotFoundException{
        dtoHandler.normalizeDTO(null, vehicle, null);

        VehicleMasterEntity entity = vehicleMapper.dtoToEntity(vehicle);

        entity = vehicleMasterRepository.save(entity);
        log.info("new user vehicle data saved with id {}", entity.getVehicleId());

        vehicleSearchRepository.save(vehicleSearchMapper.entityToSearchMeta(entity));

        return vehicleMapper.entityToDto(entity);
    }

    @Override
    @Transactional
    public Vehicle updateUserVehicle(String vehicleId, Vehicle vehicle) {
        VehicleMasterEntity updateEntity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });

        dtoHandler.normalizeDTO(vehicleId, vehicle, updateEntity);

        vehicleMapper.updateEntityFromDto(vehicle, updateEntity);

        updateEntity = vehicleMasterRepository.save(updateEntity);
        log.info("updated user vehicle master data");

        Integer count = vehicleSearchRepository.update(vehicleId, vehicleSearchMapper.dtoToSearch(vehicle));
        log.debug("updated user vehicle search index rec {}", count);

        return vehicleMapper.entityToDto(updateEntity);
    }

    @Override
    @Transactional
    public void deleteUserVehicle(String vehicleId) {
        VehicleMasterEntity existingEntity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });

        try {
            vehicleMasterRepository.delete(existingEntity);
            log.info("Vehicle with id {} is removed.", vehicleId);

            vehicleSearchRepository.delete(vehicleId);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("failed to delete user vehicle by id {}", vehicleId);
        }

    }

    @Override
    public Integer importVehicleMaster(InputStream is) throws IOException {
        log.info("importing vehicle master data ...");

        return vehicleMasterImportHandler.batchImport(is);
    }

}
