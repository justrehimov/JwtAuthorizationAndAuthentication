package com.desofme.jwtauth.controller;

import com.desofme.jwtauth.dto.request.LoginRequest;
import com.desofme.jwtauth.dto.request.RefreshTokenRequest;
import com.desofme.jwtauth.dto.request.UserRequest;
import com.desofme.jwtauth.dto.response.JwtResponse;
import com.desofme.jwtauth.dto.response.RefreshTokenResponse;
import com.desofme.jwtauth.dto.response.ResponseModel;
import com.desofme.jwtauth.dto.response.UserResponse;
import com.desofme.jwtauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseModel<UserResponse> register(@Valid @RequestBody UserRequest userRequest){
        return authService.register(userRequest);
    }

    @PostMapping("/login")
    public ResponseModel<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);
    }

    @PostMapping("/refresh")
    public ResponseModel<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        return authService.refreshToken(refreshTokenRequest);
    }

    @GetMapping("/confirm/{token}")
    public void confirm(@PathVariable("token") String token){
        authService.confirm(token);
    }
}
