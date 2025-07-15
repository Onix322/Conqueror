package framework.src.server.httpServer.utils.responseEntity;

/**
 * ResponseSuccessful is a utility class that encapsulates the successful response of an HTTP request.
 * It allows for easy construction and retrieval of HTTP status, message, and data.
 * This class is used to build a response entity that can be returned to the client.
 *
 * @param <T> the type of data contained in the response
 */
public class ResponseSuccessful<T> {
    private int httpStatus;
    private String message;
    private T data;

    public ResponseSuccessful(ResponseEntityBuilder<T> builder) {
        this.httpStatus = builder.getHttpStatus();
        this.message = builder.getMessage();
        this.data = builder.getData();
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static <T> ResponseEntityBuilder<T> builder() {
        return new ResponseEntityBuilder<>();
    }

    public static class ResponseEntityBuilder<T> {
        private int httpStatus;
        private String message;
        private T data;

        private ResponseEntityBuilder() {
        }

        public int getHttpStatus() {
            return httpStatus;
        }

        public String getMessage() {
            return message;
        }

        public T getData() {
            return data;
        }

        public ResponseEntityBuilder<T> setHttpStatus(int httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public ResponseEntityBuilder<T> setMessage(String message) {
            this.message = message;
            return this;
        }

        public ResponseEntityBuilder<T> setData(T data) {
            this.data = data;
            return this;
        }

        public ResponseSuccessful<T> build() {
            return new ResponseSuccessful<>(this);
        }

        @Override
        public String toString() {
            return "ResponseEntityBuilder{" +
                    "httpStatus=" + httpStatus +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ResponseEntity{" +
                "httpStatus=" + httpStatus +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
