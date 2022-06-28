package com.desofme.jwtauth.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
public class UserRequest {
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be empty")
    @Size(min = 2,message = "name size cannot be less than 2")
    private String name;
    @NotNull(message = "email cannot be null")
    @NotBlank(message = "email cannot be empty")
    @Size(min = 5,message = "email size cannot be less than 5")
    @Email
    private String email;
    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be empty")
    @Size(min = 6,message = "password cannot be less than 6")
    private String password;
}
