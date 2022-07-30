package com.jumkid.vehicle.exception;

import lombok.Getter;

import java.util.List;

public class VehicleImportException extends RuntimeException{

    @Getter
    private final List<String> importErrors;

    public VehicleImportException(List<String> importErrors) {
        this.importErrors = importErrors;
    }

}
