package com.PromptToApp.core.Dto.Req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

//since we have used record , an immutable object with getter and all args constructor , jackson also will read the json and validate + create dto object in 1 go
//not like normal class , that first creating an empty object then adding values by setters
public record SignUpReqDto(

        @NotBlank
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password) {


}
