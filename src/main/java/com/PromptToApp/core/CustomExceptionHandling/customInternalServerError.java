package com.PromptToApp.core.CustomExceptionHandling;

public class customInternalServerError extends RuntimeException{

    public customInternalServerError(String message){
        super(message);
    }
}
