package com.jumkid.vehicle.controller;

import com.jumkid.share.controller.response.PagingResponse;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.service.VehicleMasterService;
import com.jumkid.vehicle.service.dto.Vehicle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping("/vehicles")
public class VehicleSearchController {

    private final VehicleMasterService vehicleMasterService;


    public VehicleSearchController(VehicleMasterService vehicleMasterService) {
        this.vehicleMasterService = vehicleMasterService;
    }

    @GetMapping(value = "/search-public")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('GUEST_ROLE', 'USER_ROLE', 'ADMIN_ROLE')")
    public PagingResponse<Vehicle> searchPublicVehicles(@NotNull @Valid @RequestParam String keyword,
                                                     @Min(1) @Valid @RequestParam Integer size,
                                                     @Min(1) @Valid @RequestParam Integer page) {
        log.debug("search public vehicle by keyword {}, size {}", keyword, size);

        PagingResults<Vehicle> results = vehicleMasterService.searchPublicVehicles(keyword, size, page);

        return PagingResponse.<Vehicle>builder()
                .success(true)
                .total(results.getTotal())
                .page(results.getPage())
                .size(results.getSize())
                .data(results.getResultSet())
                .build();
    }

    @GetMapping(value = "/search")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('USER_ROLE', 'ADMIN_ROLE')")
    public PagingResponse<Vehicle> searchUserVehicles(@NotNull @Valid @RequestParam String keyword,
                                                     @Min(1) @Valid @RequestParam Integer size,
                                                     @Min(1) @Valid @RequestParam Integer page) {
        log.debug("search user vehicle by keyword {}, size {}", keyword, size);

        PagingResults<Vehicle> results = vehicleMasterService.searchUserVehicles(keyword, size, page);

        return PagingResponse.<Vehicle>builder()
                .success(true)
                .total(results.getTotal())
                .page(results.getPage())
                .size(results.getSize())
                .data(results.getResultSet())
                .build();
    }
}
