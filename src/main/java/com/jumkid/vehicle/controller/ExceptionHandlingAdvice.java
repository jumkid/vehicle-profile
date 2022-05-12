package com.jumkid.vehicle.controller;

import com.jumkid.share.controller.response.CustomErrorResponse;
import com.jumkid.share.security.exception.UserProfileNotFoundException;
import com.jumkid.vehicle.exception.ModificationDatetimeNotFoundException;
import com.jumkid.vehicle.exception.VehicleDataOutdatedException;
import com.jumkid.vehicle.exception.VehicleNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Calendar;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingAdvice {

    @ExceptionHandler({UserProfileNotFoundException.class})
    @ResponseStatus(FORBIDDEN)
    public CustomErrorResponse handle(Exception ex) {
        log.info("User profile could not be found.", ex);
        return new CustomErrorResponse(Calendar.getInstance().getTime(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public CustomErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("The provided argument is missing or invalid.", ex);
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .property(ex.getFieldErrors().stream().map(FieldError::getField).collect(Collectors.toList()))
                .details(ex.getFieldErrors().stream().map(FieldError::getDefaultMessage).collect(Collectors.toList()))
                .build();
    }

    @ExceptionHandler(VehicleDataOutdatedException.class)
    @ResponseStatus(CONFLICT)
    public CustomErrorResponse handleMethodArgumentNotValidException(VehicleDataOutdatedException ex) {
        log.warn("The target vehicle data has been updated.", ex);
        return new CustomErrorResponse(Calendar.getInstance().getTime(), ex.getMessage());
    }

    @ExceptionHandler({VehicleNotFoundException.class, ModificationDatetimeNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    public CustomErrorResponse handleNotFoundException(RuntimeException ex){
        return new CustomErrorResponse(Calendar.getInstance().getTime(), ex.getMessage());
    }
}
