package com.PromptToApp.core.CustomExceptionHandling;

import lombok.*;
import org.springframework.http.HttpStatus;


@Builder
public record ApiError(String message , HttpStatus status)





{
}
