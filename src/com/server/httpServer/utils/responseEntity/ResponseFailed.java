package src.com.server.httpServer.utils.responseEntity;

import src.com.server.httpServer.utils.response.HttpStatus;

public class ResponseFailed {
    private int status;
    private String message;
    private String error;

    public ResponseFailed(HttpStatus status, String error) {
        this.status = status.getCode();
        this.message = status.getMessage();
        this.error = error;
    }

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

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
