package com.desofme.jwtauth.service.impl;

import com.desofme.jwtauth.auth.Role;
import com.desofme.jwtauth.exception.CustomException;
import com.desofme.jwtauth.exception.StatusCode;
import com.desofme.jwtauth.exception.StatusMessage;
import com.desofme.jwtauth.repository.RoleRepo;
import com.desofme.jwtauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;

    @Override
    public Role saveRole(Role role) {
        Role savedRole = null;
        if(!roleRepo.findByRoleName(role.getRoleName()).isPresent())
               savedRole = roleRepo.save(role);
        return savedRole;
    }

    @Override
    public Role getRoleByName(String roleName) {
        return roleRepo.findByRoleName(roleName)
                .orElseThrow(()->new CustomException(StatusMessage.ROLE_NOT_FOUND, StatusCode.ROLE_NOT_FOUND));
    }
}
