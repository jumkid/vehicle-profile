package com.jumkid.vehicle.exception;

import com.jumkid.vehicle.model.VehicleSearch;
import lombok.Getter;

@Getter
public class VehicleSearchException extends RuntimeException {

    private final VehicleSearch vehicleSearch;

    public VehicleSearchException(String errorMsg) {
        super(errorMsg);
        this.vehicleSearch = null;
    }

    public VehicleSearchException(String errorMsg, VehicleSearch vehicleSearch){
        super(errorMsg);
        this.vehicleSearch = vehicleSearch;
    }
	
}
