package com.desofme.jwtauth.service.impl;

import com.desofme.jwtauth.auth.User;
import com.desofme.jwtauth.dto.response.ResponseModel;
import com.desofme.jwtauth.dto.response.ResponseStatus;
import com.desofme.jwtauth.dto.response.UserResponse;
import com.desofme.jwtauth.enums.EnumStatus;
import com.desofme.jwtauth.exception.CustomException;
import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import com.desofme.jwtauth.repository.UserRepo;
import com.desofme.jwtauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public User getUser(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(()->new CustomException(StatusMessage.USER_NOT_FOUND, StatusCode.USER_NOT_FOUND));
    }

    @Override
    public ResponseModel<List<UserResponse>> getUsers() {
        List<User> users = userRepo.findUserByStatus(EnumStatus.ACTIVE.getValue());
        if(users.isEmpty())
            throw new CustomException(StatusMessage.USER_NOT_FOUND, StatusCode.USER_NOT_FOUND);
        List<UserResponse> userResponses = users.stream()
                .map(u->modelMapper.map(u, UserResponse.class))
                .collect(Collectors.toList());
        return ResponseModel.<List<UserResponse>>builder()
                .response(userResponses)
                .status(ResponseStatus.getSuccess())
                .build();
    }
}
