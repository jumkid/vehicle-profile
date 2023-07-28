package com.jumkid.vehicle.exception;

public class VehicleGalleryNoEmptyException extends RuntimeException{

    private static final String ERROR = "Target vehicle gallery is not empty.";

    public VehicleGalleryNoEmptyException() { super(ERROR); }
}
