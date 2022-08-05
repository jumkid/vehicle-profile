package com.jumkid.vehicle.service;

import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.exception.VehicleSearchException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.model.VehicleSearch;
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
                                    UserProfileManager userProfileManager,
                                    VehicleMasterImportHandler vehicleMasterImportHandler,
                                    DTOHandler dtoHandler, VehicleMapper vehicleMapper,
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
        String userId = getCurrentUserId();

        List<VehicleMasterEntity> vehicleMasterEntities = vehicleMasterRepository.findByCreatedBy(userId);
        log.info("Found {} vehicles for user {}", vehicleMasterEntities.size(), userId);

        return vehicleMapper.entitiesToDTOs(vehicleMasterEntities);
    }

    @Override
    public PagingResults<Vehicle> searchUserVehicles(String keyword, Integer size, Integer page) throws VehicleSearchException{
        String userId = getCurrentUserId();
        PagingResults<Vehicle> pagingResults = PagingResults.<Vehicle>builder()
                                                        .page(page)
                                                        .size(size)
                                                        .build();
        try {
            PagingResults<VehicleSearch> searchResult = vehicleSearchRepository.search(keyword, size, page, userId);

            if (!searchResult.getResultSet().isEmpty()) {
                List<Vehicle> results = searchResult.getResultSet().stream()
                        .map(vehicleSearch -> this.getUserVehicle(vehicleSearch.getId()))
                        .collect(Collectors.toList());

                pagingResults.setTotal(searchResult.getTotal());
                pagingResults.setResultSet(results);

            } else {
                pagingResults.setTotal(0L);
                pagingResults.setResultSet(Collections.emptyList());

            }
            return pagingResults;

        } catch (Exception e) {
            e.printStackTrace();
            throw new VehicleSearchException(e.getMessage());
        }

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

    private String getCurrentUserId() {
        UserProfile userProfile = userProfileManager.fetchUserProfile();
        return userProfile.getId();
    }

}
