package com.jumkid.vehicle.repository;

import com.jumkid.vehicle.model.VehicleMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleMasterRepository extends JpaRepository<VehicleMasterEntity, String> {

    List<VehicleMasterEntity> findByCreatedByOrderByCreationDate(String userId);

}
