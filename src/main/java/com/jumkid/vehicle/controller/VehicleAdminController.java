package com.jumkid.vehicle.controller;

import com.jumkid.share.controller.response.CommonResponse;
import com.jumkid.vehicle.exception.BatchProcessException;
import com.jumkid.vehicle.service.VehicleMasterService;
import com.jumkid.vehicle.service.batch.OnDemandBatchService;
import com.jumkid.vehicle.service.dto.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vehicles")
public class VehicleAdminController {

    private final VehicleMasterService vehicleMasterService;
    private final OnDemandBatchService vehicleProfileReindexBatchService;

    public VehicleAdminController(VehicleMasterService vehicleMasterService,
                                  OnDemandBatchService vehicleProfileReindexBatchService) {
        this.vehicleMasterService = vehicleMasterService;
        this.vehicleProfileReindexBatchService = vehicleProfileReindexBatchService;
    }

    @PostMapping(value = "import")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public CommonResponse importVehicles(@NotNull @RequestParam("file") MultipartFile file) {
        try {
            Integer count = vehicleMasterService.importVehicleMaster(file.getInputStream());
            return CommonResponse.builder()
                    .success(true).msg(count.toString())
                    .build();
        } catch (IOException ioe) {
            log.error("Not able to load data from csv: {}", ioe.getMessage());
            return CommonResponse.builder()
                    .success(false).msg("Failed to import vehicle data from csv")
                    .build();
        }
    }

    @PostMapping(value = "ingest")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public CommonResponse ingestVehicles(@NotNull List<Vehicle> vehicles) {
        Integer count = vehicleMasterService.importVehicleMaster(vehicles);
        return CommonResponse.builder()
                .success(true).msg(count.toString())
                .build();
    }

    @GetMapping(value = "/reindex")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ADMIN_ROLE')")
    public CommonResponse reindexAllVehicles() {
        try {
            int count = vehicleProfileReindexBatchService.runJob();
            return CommonResponse.builder()
                    .success(true)
                    .data(count)
                    .build();
        } catch (BatchProcessException bpe) {
            return CommonResponse.builder()
                    .success(false)
                    .msg("Failed to reindex vehicle data")
                    .build();
        }

    }
}
