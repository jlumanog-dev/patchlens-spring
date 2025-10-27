package com.jlumanog_dev.patchlens_spring_backend.exception;

public class AuthErrorResponse {
    private int status;
    private String message;
    private long timestamp;

    public AuthErrorResponse(){}

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
