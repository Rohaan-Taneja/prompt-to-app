package com.PromptToApp.core.CustomExceptionHandling;

public class customUnauthorizedException extends RuntimeException {

    public customUnauthorizedException(String message) {
        super(message);
    }
}
