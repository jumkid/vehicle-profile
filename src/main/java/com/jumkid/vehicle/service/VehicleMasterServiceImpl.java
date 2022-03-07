package com.jumkid.vehicle.service;

import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.mapper.VehicleMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VehicleMasterServiceImpl implements VehicleMasterService{

    private final VehicleMasterRepository vehicleMasterRepository;

    private final UserProfileManager userProfileManager;

    private final VehicleMapper vehicleMapper = Mappers.getMapper(VehicleMapper.class);

    @Autowired
    public VehicleMasterServiceImpl(VehicleMasterRepository vehicleMasterRepository, UserProfileManager userProfileManager) {
        this.vehicleMasterRepository = vehicleMasterRepository;
        this.userProfileManager = userProfileManager;
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
    @Transactional
    public Vehicle saveUserVehicle(Vehicle vehicle) throws UserProfileNotFoundException{
        normalizeDTO(null, vehicle, null);

        VehicleMasterEntity entity = vehicleMapper.dtoToEntity(vehicle);

        entity = vehicleMasterRepository.save(entity);
        log.debug("new user vehicle saved");

        return vehicleMapper.entityToDto(entity);
    }

    @Override
    public void deleteUserVehicle(String vehicleId) {
        VehicleMasterEntity existingEntity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
        vehicleMasterRepository.delete(existingEntity);
        log.debug("Vehicle with id {} is removed.", vehicleId);
    }

    private UserProfile getUserProfile() {
        UserProfile userProfile = userProfileManager.fetchUserProfile();
        if (userProfile == null) {
            throw new UserProfileNotFoundException("User profile could not be found. Access is denied");
        } else {
            return userProfile;
        }
    }

    private void normalizeDTO(String vehicleId, Vehicle dto, VehicleMasterEntity oldEntity) {
        if (vehicleId != null) dto.setId(vehicleId);

        LocalDateTime now = LocalDateTime.now();
        UserProfile userProfile = userProfileManager.fetchUserProfile();
        String userId = userProfile != null ? userProfile.getId() : null;

        dto.setModifiedBy(userId);
        dto.setModificationDate(now);

        if (oldEntity != null) {
            dto.setCreatedBy(oldEntity.getCreatedBy());
            dto.setCreationDate(oldEntity.getCreationDate());
        } else {
            dto.setCreatedBy(userId);
            dto.setCreationDate(now);
        }
    }
}
