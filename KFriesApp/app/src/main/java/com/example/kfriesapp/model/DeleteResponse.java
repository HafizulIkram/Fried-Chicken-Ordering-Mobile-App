package com.example.kfriesapp.model;

public class DeleteResponse {
    private int status;
    private Success success;

    public DeleteResponse() {
    }

    public DeleteResponse(int status, Success success) {
        this.status = status;
        this.success = success;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "DeleteResponse{" +
                "status=" + status +
                ", success=" + success +
                '}';
    }
}

class Success {
    public int code;
    public String status;

    public Success() {
    }

    public Success(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Success{" +
                "code=" + code +
                ", status='" + status + '\'' +
                '}';
    }
}