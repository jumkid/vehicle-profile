package com.jumkid.vehicle.controller;

import com.jumkid.share.controller.response.CommonResponse;
import com.jumkid.vehicle.service.VehicleMasterService;
import com.jumkid.vehicle.service.dto.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vehicles")
@Validated
public class VehicleController {

    private final VehicleMasterService vehicleMasterService;

    @Autowired
    public VehicleController(VehicleMasterService vehicleMasterService) {
        this.vehicleMasterService = vehicleMasterService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Vehicle> getUserVehicles() {
        log.debug("fetch all user vehicles");

        return vehicleMasterService.getUserVehicles();
    }

    @GetMapping(value = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('GUEST_ROLE', 'USER_ROLE', 'ADMIN_ROLE')")
    @PostAuthorize("@vehicleAccessPermissionAuthorizer.isPublic(returnObject)" +
            " || (hasAuthority('USER_ROLE') && @vehicleAccessPermissionAuthorizer.isOwner(returnObject))")
    public Vehicle getUserVehicle(@NotNull @Valid @PathVariable String id) {
        return vehicleMasterService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('USER_ROLE', 'ADMIN_ROLE')")
    public Vehicle save(@NotNull @Valid @RequestBody Vehicle vehicle){
        return vehicleMasterService.save(vehicle);
    }

    @PostMapping("/save-as-new")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('USER_ROLE', 'ADMIN_ROLE') && @vehicleAccessPermissionAuthorizer.isPublic(#vehicle)")
    public Vehicle saveAsNew(@NotNull @Valid @RequestBody Vehicle vehicle){
        return vehicleMasterService.saveAsNew(vehicle);
    }

    @PutMapping(value = "{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PreAuthorize("hasAuthority('ADMIN_ROLE')" +
            " || (hasAuthority('USER_ROLE') && @vehicleAccessPermissionAuthorizer.isOwner(#id))")
    public Vehicle update(@NotNull @Valid @PathVariable String id,
                        @NotNull @RequestBody Vehicle partialVehicle){
        return vehicleMasterService.update(id, partialVehicle);
    }

    @DeleteMapping(value = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ADMIN_ROLE')" +
            " || (hasAuthority('USER_ROLE') && @vehicleAccessPermissionAuthorizer.isOwner(#id))")
    public CommonResponse deleteUserVehicle(@PathVariable String id) {
        vehicleMasterService.delete(id);
        return CommonResponse.builder().success(true).data(String.valueOf(id)).build();
    }

}
