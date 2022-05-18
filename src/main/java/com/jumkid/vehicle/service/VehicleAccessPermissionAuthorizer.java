package com.jumkid.vehicle.service;

import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.enums.AccessScope;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import org.springframework.stereotype.Component;

@Component(value = "vehicleAccessPermissionAuthorizer")
public class VehicleAccessPermissionAuthorizer {

    private final UserProfileManager userProfileManager;

    private final VehicleMasterRepository vehicleMasterRepository;

    public VehicleAccessPermissionAuthorizer(UserProfileManager userProfileManager, VehicleMasterRepository vehicleMasterRepository) {
        this.userProfileManager = userProfileManager;
        this.vehicleMasterRepository = vehicleMasterRepository;
    }

    public boolean hasViewPermission(Vehicle vehicle) {
        return vehicle.getAccessScope().equals(AccessScope.PUBLIC) || checkUserId(vehicle.getCreatedBy());
    }

    public boolean hasUpdatePermission(String vehicleId) {
        VehicleMasterEntity entity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });
        return checkUserId(entity.getCreatedBy());
    }

    private boolean checkUserId(String userId) {
        return this.getUserProfile().getId().equals(userId);
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
