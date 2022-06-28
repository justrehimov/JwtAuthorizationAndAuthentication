package com.desofme.jwtauth.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginRequest {
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be empty")
    @Size(min = 2,message = "name size cannot be less than 2")
    private String username;
    @NotNull(message = "password cannot be null")
    @NotBlank(message = "password cannot be empty")
    private String password;
}
