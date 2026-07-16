package com.PromptToApp.core.Dto.Req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record loginReqDto(

        @Email
        @NotNull
        String email,

        @NotNull
        @Min(value = 4)
        String password

) {
}
