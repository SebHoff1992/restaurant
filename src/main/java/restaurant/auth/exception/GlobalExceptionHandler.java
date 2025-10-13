package restaurant.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import restaurant.common.api.ApiErrorResponse;

import java.util.Collections;

/**
 * Global exception handler returning standardized JSON error responses.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, "AUTH-4041", ex.getMessage(), request, null);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRoleNotFound(RoleNotFoundException ex, WebRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, "AUTH-4002", ex.getMessage(), request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "GEN-5000",
                "Unexpected error: " + ex.getMessage(), request,
                Collections.singletonList("Please contact support or try again later."));
    }

    // --- Helper method ---
    private ResponseEntity<ApiErrorResponse> buildResponse(
            HttpStatus status, String errorCode, String message, WebRequest request, java.util.List<String> details) {

        String path = request.getDescription(false).replace("uri=", "");
        ApiErrorResponse response = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .errorCode(errorCode)
                .details(details)
                .build();

        return new ResponseEntity<>(response, status);
    }
}
