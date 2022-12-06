package com.jumkid.vehicle.service.handler;

import com.jumkid.share.user.UserProfile;
import com.jumkid.share.user.UserProfileManager;
import com.jumkid.vehicle.exception.ModificationDatetimeNotFoundException;
import com.jumkid.vehicle.exception.VehicleDataOutdatedException;
import com.jumkid.vehicle.model.VehicleMasterEntity;
import com.jumkid.vehicle.service.dto.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class DTOHandler {

    private final UserProfileManager userProfileManager;

    public DTOHandler(UserProfileManager userProfileManager) {
        this.userProfileManager = userProfileManager;
    }

    public void normalizeDTO(String vehicleId, Vehicle dto, VehicleMasterEntity oldEntity) {
        if (vehicleId != null) { dto.setId(vehicleId); }
        if (vehicleId == null && oldEntity == null) { dto.setId(null); }

        LocalDateTime now = LocalDateTime.now();
        UserProfile userProfile = userProfileManager.fetchUserProfile();
        String userId = userProfile.getId();

        if (oldEntity != null) {
            dto.setCreatedBy(oldEntity.getCreatedBy());
            dto.setCreationDate(oldEntity.getCreationDate());

            if (dto.getModificationDate() == null) { throw new ModificationDatetimeNotFoundException(); }

            if (!oldEntity.getModificationDate().truncatedTo(ChronoUnit.MILLIS)
                    .equals(dto.getModificationDate().truncatedTo(ChronoUnit.MILLIS))) {
                throw new VehicleDataOutdatedException();
            }
        } else {
            dto.setCreationDate(now);
            dto.setCreatedBy(userId);
        }

        dto.setModifiedBy(userId);
        dto.setModificationDate(now.truncatedTo(ChronoUnit.MILLIS));
    }

}
