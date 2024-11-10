package com.comark.app.exception;

public class ComarkAppException extends RuntimeException {
    private int statusCode;
    private String errorMessage;

    // Constructor with error message and status code
    public ComarkAppException(int statusCode, String errorMessage) {
        super(errorMessage);
        this.statusCode = statusCode;
        this.errorMessage = errorMessage;
    }
    // Getter for status code
    public int getStatusCode() {
        return statusCode;
    }

    // Getter for error message
    public String getErrorMessage() {
        return errorMessage;
    }
}
