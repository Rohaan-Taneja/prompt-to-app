package com.PromptToApp.core.CustomExceptionHandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(Exception exception) {
        ApiError error = ApiError.builder().message(exception.getMessage()).status(HttpStatus.NOT_FOUND).build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ResourceAlreadyPresent.class)
    public ResponseEntity<ApiError> handleResourceAlreadyPresentException(Exception exception) {
        ApiError error = ApiError.builder().message(exception.getMessage()).status(HttpStatus.CONFLICT).build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(customUnauthorizedException.class)
    public ResponseEntity<ApiError> handleCustomUnauthorizedException(Exception exception) {
        ApiError error = ApiError.builder().message(exception.getMessage()).status(HttpStatus.UNAUTHORIZED).build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(StripeCustomException.class)
    public ResponseEntity<ApiError> handleStripeCustomException(Exception exception) {
        ApiError error = ApiError.builder().message(exception.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }



    @ExceptionHandler(customBadRequestException.class)
    public ResponseEntity<ApiError> handleCustomBadRequestException(Exception exception) {
        ApiError error = ApiError.builder().message(exception.getMessage()).status(HttpStatus.BAD_REQUEST).build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(customInternalServerError.class)
    public ResponseEntity<ApiError> handleCustomInternalServerException(Exception exception) {
        ApiError error = ApiError.builder().message(exception.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR).build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
