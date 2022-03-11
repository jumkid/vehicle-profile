package com.jumkid.vehicle.exception;

public class VehicleDataOutdatedException extends RuntimeException{
    private static final String ERROR = "Vehicle data is outdated.";

    public VehicleDataOutdatedException() { super(ERROR); }
}
