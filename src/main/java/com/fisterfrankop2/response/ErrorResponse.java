package com.fisterfrankop2.response;

public class ErrorResponse {
    private String error = null;
    private Object details = null;

    // Constructor for message-only error
    public ErrorResponse(String error) {
        this.error = error;
    }

    // Constructor for error with additional details
    public ErrorResponse(String error, Object details) {
        this.error = error;
        this.details = details;
    }

    public String getError() {
        return error;
    }

    public Object getDetails() {
        return details;
    }
}
