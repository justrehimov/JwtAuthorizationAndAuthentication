package com.desofme.jwtauth.service;

import com.desofme.jwtauth.auth.RefreshToken;

public interface RefreshTokenService {
    RefreshToken getRefreshToken(String token);
    Boolean isExpiredToken(String token);
    Boolean isValidToken(String token);
}
