package com.desofme.jwtauth.service.impl;

import com.desofme.jwtauth.auth.ConfirmationToken;
import com.desofme.jwtauth.exception.CustomException;
import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import com.desofme.jwtauth.repository.ConfirmationTokenRepo;
import com.desofme.jwtauth.service.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    private final ConfirmationTokenRepo tokenRepo;

    @Override
    public ConfirmationToken getToken(String token) {
        return tokenRepo.findByToken(token)
                .orElseThrow(()->new CustomException(StatusMessage.TOKEN_NOT_FOUND, StatusCode.TOKEN_NOT_FOUND));
    }

    @Override
    public Boolean isExpired(ConfirmationToken confirmationToken) {
        return confirmationToken.getExpiredAt().before(new Date());
    }

    @Override
    public Boolean isValid(ConfirmationToken confirmationToken) {
        return confirmationToken!=null && !isExpired(confirmationToken);
    }
}
