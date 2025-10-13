package restaurant.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a standardized JSON structure for all API error responses.
 * Includes additional metadata like errorCode and details for validation errors.
 */
public class ApiErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;      // unique internal error identifier
    private List<String> details;  // additional validation or contextual messages

    // --- Constructors ---
    public ApiErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(int status, String error, String message, String path,
                            String errorCode, List<String> details) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errorCode = errorCode;
        this.details = details;
    }

    // --- Builder pattern for convenience ---
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int status;
        private String error;
        private String message;
        private String path;
        private String errorCode;
        private List<String> details;

        public Builder status(int status) { this.status = status; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public Builder errorCode(String errorCode) { this.errorCode = errorCode; return this; }
        public Builder details(List<String> details) { this.details = details; return this; }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(status, error, message, path, errorCode, details);
        }
    }

    // --- Getters ---
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public String getErrorCode() { return errorCode; }
    public List<String> getDetails() { return details; }
}
