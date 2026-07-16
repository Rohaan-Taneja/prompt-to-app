package com.PromptToApp.core.CustomExceptionHandling;

public class customBadRequestException extends RuntimeException {
    public customBadRequestException(String message) {
        super(message);
    }
}
