package com.desofme.jwtauth.controller;

import com.desofme.jwtauth.auth.User;
import com.desofme.jwtauth.dto.response.ResponseModel;
import com.desofme.jwtauth.dto.response.UserResponse;
import com.desofme.jwtauth.repository.UserRepo;
import com.desofme.jwtauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/list")
    public ResponseModel<List<UserResponse>> getUsers(){
        return userService.getUsers();
    }

}
