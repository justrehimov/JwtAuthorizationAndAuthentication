package com.desofme.jwtauth.service.impl;

import com.desofme.jwtauth.auth.RefreshToken;
import com.desofme.jwtauth.exception.CustomException;
import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import com.desofme.jwtauth.repository.RefreshTokenRepo;
import com.desofme.jwtauth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    public RefreshToken getRefreshToken(String token) {
        return refreshTokenRepo.findByToken(token)
                .orElseThrow(()->new CustomException(StatusMessage.REFRESH_TOKEN_NOT_FOUND, StatusCode.REFRESH_TOKEN_NOT_FOUND));

    }

    @Override
    public Boolean isExpiredToken(String token) {
       RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(()->new CustomException(StatusMessage.REFRESH_TOKEN_NOT_FOUND, StatusCode.REFRESH_TOKEN_NOT_FOUND));
       return refreshToken.getExpiredAt().before(new Date());
    }

    @Override
    public Boolean isValidToken(String token) {
        return getRefreshToken(token) != null && !isExpiredToken(token);
    }
}
