package com.jumkid.vehicle.exception;

public class BatchProcessException extends RuntimeException {
    private static final String ERROR = "Batch job failed";

    public BatchProcessException() { super(ERROR); }

    public BatchProcessException(String error) { super(error); }
}
