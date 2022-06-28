package com.desofme.jwtauth.service.impl;

import com.desofme.jwtauth.auth.User;
import com.desofme.jwtauth.enums.EnumStatus;
import com.desofme.jwtauth.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsernameAndStatus(username, EnumStatus.ACTIVE.getValue())
                .orElseThrow(()->new UsernameNotFoundException(String.format("User not found with {0} username",username)));
        return user;
    }
}
