package com.desofme.jwtauth.service;

import com.desofme.jwtauth.auth.User;
import com.desofme.jwtauth.dto.response.ResponseModel;
import com.desofme.jwtauth.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    User getUser(String username);

    ResponseModel<List<UserResponse>> getUsers();
}
