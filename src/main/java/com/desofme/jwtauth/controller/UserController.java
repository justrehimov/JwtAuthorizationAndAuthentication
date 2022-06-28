package com.desofme.jwtauth.controller;

import com.desofme.jwtauth.auth.User;
import com.desofme.jwtauth.dto.response.ResponseModel;
import com.desofme.jwtauth.dto.response.UserResponse;
import com.desofme.jwtauth.repository.UserRepo;
import com.desofme.jwtauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/list")
    public ResponseModel<List<UserResponse>> getUsers(){
        return userService.getUsers();
    }

}
