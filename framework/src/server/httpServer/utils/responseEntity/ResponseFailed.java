package framework.src.server.httpServer.utils.responseEntity;

import framework.src.server.httpServer.utils.response.HttpStatus;

/**
 * ResponseFailed is a utility class that encapsulates the details of a failed HTTP response.
 * It includes the HTTP status code, a message, and an error description.
 */
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
