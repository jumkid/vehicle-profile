package com.jumkid.vehicle.exception;

import com.jumkid.vehicle.model.VehicleSearch;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
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
