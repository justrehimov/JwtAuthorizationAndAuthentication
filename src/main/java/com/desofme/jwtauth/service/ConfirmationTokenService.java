package com.desofme.jwtauth.service;

import com.desofme.jwtauth.auth.ConfirmationToken;

public interface ConfirmationTokenService {

    ConfirmationToken getToken(String token);

    Boolean isExpired(ConfirmationToken confirmationToken);

    Boolean isValid(ConfirmationToken confirmationToken);
}
