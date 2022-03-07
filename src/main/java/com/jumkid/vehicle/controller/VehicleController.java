package com.jumkid.vehicle.controller;

import com.jumkid.share.controller.response.CommonResponse;
import com.jumkid.vehicle.service.VehicleMasterService;
import com.jumkid.vehicle.service.dto.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    private final VehicleMasterService vehicleMasterService;


    @Autowired
    public VehicleController(VehicleMasterService vehicleMasterService) {
        this.vehicleMasterService = vehicleMasterService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Vehicle> getUserVehicles() {
        return vehicleMasterService.getUserVehicles();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle save(@NotNull @Valid @RequestBody Vehicle vehicle){
        return vehicleMasterService.saveUserVehicle(vehicle);
    }

    @DeleteMapping(value = "{vehicleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse deleteUserVehicle(@PathVariable String vehicleId) {
        vehicleMasterService.deleteUserVehicle(vehicleId);
        return CommonResponse.builder().success(true).data(String.valueOf(vehicleId)).build();
    }


}
