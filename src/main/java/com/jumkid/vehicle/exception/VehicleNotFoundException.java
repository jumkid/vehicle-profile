package com.jumkid.vehicle.exception;

public class VehicleNotFoundException extends RuntimeException{

    private static final String ERROR = "Can not find vehicle with Id: ";

    public VehicleNotFoundException(String vehicleId) { super(ERROR + vehicleId); }

}
