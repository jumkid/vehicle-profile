package com.jumkid.vehicle.exception;

import com.jumkid.share.controller.response.CustomErrorResponse;
import com.jumkid.share.exception.ModificationDatetimeNotFoundException;
import com.jumkid.share.exception.ModificationDatetimeOutdatedException;
import com.jumkid.share.security.exception.UserProfileNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlingAdvice {

    @ExceptionHandler({UserProfileNotFoundException.class, AccessDeniedException.class})
    @ResponseStatus(FORBIDDEN)
    public CustomErrorResponse handleUserProfileNotFound(RuntimeException ex) {
        log.info("User profile could not be found.", ex);
        return new CustomErrorResponse(Calendar.getInstance().getTime(), ex.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    public CustomErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.warn("The provided argument is missing or invalid.", ex);
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .property(ex.getFieldErrors().stream().map(FieldError::getField).toList())
                .details(ex.getFieldErrors().stream().map(FieldError::getDefaultMessage).toList())
                .build();
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(BAD_REQUEST)
    public CustomErrorResponse handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .property(List.of(e.getParameterName()))
                .details(List.of(Objects.requireNonNull(e.getMessage())))
                .build();
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(BAD_REQUEST)
    public CustomErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .message(ex.getParameter().getParameterName())
                .property(List.of(Objects.requireNonNull(ex.getParameter().getParameterName())))
                .details(List.of(Objects.requireNonNull(ex.getMessage())))
                .build();
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(BAD_REQUEST)
    public CustomErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn("The provided argument is invalid.", ex);
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .property(ex.getConstraintViolations().stream()
                        .map(constraintViolation -> constraintViolation.getPropertyPath().toString())
                        .toList())
                .details(ex.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .toList())
                .build();
    }

    @ExceptionHandler({MultipartException.class})
    @ResponseStatus(BAD_REQUEST)
    public CustomErrorResponse handleMultipartException(MultipartException ex) {
        return CustomErrorResponse.builder()
                .message("Upload failed due to: " + ex.getMessage())
                .build();
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(BAD_REQUEST)
    public CustomErrorResponse handleMessageNotReadableException(HttpMessageNotReadableException ex) {
        return CustomErrorResponse.builder()
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ModificationDatetimeOutdatedException.class)
    @ResponseStatus(CONFLICT)
    public CustomErrorResponse handleMethodArgumentNotValidException(ModificationDatetimeOutdatedException ex) {
        log.warn("The target vehicle data has been updated.", ex);
        return new CustomErrorResponse(Calendar.getInstance().getTime(), ex.getMessage());
    }

    @ExceptionHandler({VehicleNotFoundException.class, ModificationDatetimeNotFoundException.class})
    @ResponseStatus(NOT_FOUND)
    public CustomErrorResponse handleNotFoundException(RuntimeException ex){
        return new CustomErrorResponse(Calendar.getInstance().getTime(), ex.getMessage());
    }

    @ExceptionHandler({VehicleImportException.class})
    @ResponseStatus(NOT_ACCEPTABLE)
    public CustomErrorResponse handleVehicleImportException(VehicleImportException vie) {
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .details(vie.getImportErrors())
                .build();
    }

    @ExceptionHandler({VehicleGalleryNoEmptyException.class})
    @ResponseStatus(CONFLICT)
    public CustomErrorResponse handleVehicleGalleryNoEmptyException(VehicleGalleryNoEmptyException ex) {
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler({Exception.class})
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public CustomErrorResponse handle(Exception e) {
        return CustomErrorResponse.builder()
                .timestamp(Calendar.getInstance().getTime())
                .message("Oops! Backend system failed to process the request. Please contact system admin")
                .build();
    }
}
