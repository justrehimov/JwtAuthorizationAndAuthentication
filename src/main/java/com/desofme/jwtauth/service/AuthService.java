package com.desofme.jwtauth.service;

import com.desofme.jwtauth.dto.request.LoginRequest;
import com.desofme.jwtauth.dto.request.RefreshTokenRequest;
import com.desofme.jwtauth.dto.request.UserRequest;
import com.desofme.jwtauth.dto.response.JwtResponse;
import com.desofme.jwtauth.dto.response.RefreshTokenResponse;
import com.desofme.jwtauth.dto.response.ResponseModel;
import com.desofme.jwtauth.dto.response.UserResponse;

public interface AuthService {
    ResponseModel<UserResponse> register(UserRequest userRequest);

    void confirm(String token);

    ResponseModel<JwtResponse> login(LoginRequest loginRequest);

    ResponseModel<RefreshTokenResponse> refreshToken(RefreshTokenRequest refreshTokenRequest);
}
