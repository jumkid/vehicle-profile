package com.jumkid.vehicle.service;

import com.jumkid.share.security.AccessScope;
import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.repository.VehicleMasterRepository;
import com.jumkid.vehicle.service.dto.Vehicle;
import org.springframework.stereotype.Component;

@Component(value = "vehicleAccessPermissionAuthorizer")
public class VehicleAccessPermissionAuthorizer {

    private final UserProfileManager userProfileManager;

    private final VehicleMasterRepository vehicleMasterRepository;

    public VehicleAccessPermissionAuthorizer(UserProfileManager userProfileManager,
                                             VehicleMasterRepository vehicleMasterRepository) {
        this.userProfileManager = userProfileManager;
        this.vehicleMasterRepository = vehicleMasterRepository;
    }

    public boolean isPublic(Vehicle vehicle) {
        return vehicle.getAccessScope().equals(AccessScope.PUBLIC);
    }

    public boolean isOwner(String vehicleId) {
        VehicleMasterEntity entity = vehicleMasterRepository.findById(vehicleId)
                .orElseThrow(() -> { throw new VehicleNotFoundException(vehicleId); });
        return isCurrentUserOwned(entity.getCreatedBy());
    }

    public boolean isOwner(Vehicle vehicle) {
        return isCurrentUserOwned(vehicle.getCreatedBy());
    }

    private boolean isCurrentUserOwned(String userId) {
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
