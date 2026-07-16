package com.PromptToApp.core.CustomExceptionHandling;

public class ResourceAlreadyPresent extends RuntimeException {
    public ResourceAlreadyPresent(String message) {
        super(message);
    }
}
