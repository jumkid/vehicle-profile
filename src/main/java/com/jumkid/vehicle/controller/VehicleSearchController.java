package com.jumkid.vehicle.controller;

import com.jumkid.share.controller.response.PagingResponse;
import com.jumkid.share.service.dto.PagingResults;
import com.jumkid.vehicle.enums.KeywordMode;
import com.jumkid.vehicle.enums.VehicleField;
import com.jumkid.vehicle.service.VehicleMasterService;
import com.jumkid.vehicle.service.dto.Vehicle;
import com.jumkid.vehicle.service.dto.VehicleFieldValuePair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vehicles")
@Validated
public class VehicleSearchController {

    private final VehicleMasterService vehicleMasterService;


    public VehicleSearchController(VehicleMasterService vehicleMasterService) {
        this.vehicleMasterService = vehicleMasterService;
    }

    @GetMapping(value = "/search-public")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('GUEST_ROLE', 'USER_ROLE', 'ADMIN_ROLE')")
    public PagingResponse<Vehicle> searchPublicVehicles(@NotNull @Valid @RequestParam String keyword,
                                                        @Valid @RequestParam(required = false) KeywordMode keywordMode,
                                                     @Min(1) @Valid @RequestParam Integer size,
                                                     @Min(1) @Valid @RequestParam Integer page) {
        log.debug("search public vehicle by keyword {}, size {}", keyword, size);

        PagingResults<Vehicle> results = vehicleMasterService.searchPublicVehicles(keyword, keywordMode, size, page);

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
                                                      @Valid @RequestParam(required = false) KeywordMode keywordMode,
                                                     @Min(1) @Valid @RequestParam Integer size,
                                                     @Min(1) @Valid @RequestParam Integer page) {
        log.debug("search user vehicle by keyword {}, size {}", keyword, size);

        PagingResults<Vehicle> results = vehicleMasterService.searchUserVehicles(keyword, keywordMode, size, page);

        return PagingResponse.<Vehicle>builder()
                .success(true)
                .total(results.getTotal())
                .page(results.getPage())
                .size(results.getSize())
                .data(results.getResultSet())
                .build();
    }

    @PostMapping(value = "/search-aggregation", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('GUEST_ROLE', 'USER_ROLE', 'ADMIN_ROLE')")
    public List<String> searchForAggregation(@NotNull @Valid @RequestParam VehicleField field,
                                             @Min(1) @RequestParam(required = false) Integer size,
                                             @RequestBody(required = false) List<@Valid VehicleFieldValuePair<String>> matchFields) {
        log.debug("search aggregation for field {}, match fields {}", field, matchFields);
        if (matchFields == null) matchFields = Collections.emptyList();
        return vehicleMasterService.searchForAggregation(field, matchFields, size);
    }

    @PostMapping(value = "/search-matchers", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('GUEST_ROLE', 'USER_ROLE', 'ADMIN_ROLE')")
    public PagingResponse<Vehicle> searchByMatchers(@Min(1) @Valid @RequestParam Integer size,
                                                    @Min(1) @Valid @RequestParam Integer page,
                                                    @RequestBody List<@Valid VehicleFieldValuePair<String>> matchFields) {
        log.debug("search by match fields {}", matchFields);
        PagingResults<Vehicle> results = vehicleMasterService.searchByMatchFields(size, page, matchFields);

        return PagingResponse.<Vehicle>builder()
                .success(true)
                .total(results.getTotal())
                .page(results.getPage())
                .size(results.getSize())
                .data(results.getResultSet())
                .build();
    }
}
