package com.fisterfrankop2.response;

public class SuccessResponse {
    private String success = null;
    private Object data = null;

    // Constructor for message-only success
    public SuccessResponse(String success) {
        this.success = success;
    }

    // Constructor for success with additional data
    public SuccessResponse(String success, Object data) {
        this.success = success;
        this.data = data;
    }

    public String getSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }
}
