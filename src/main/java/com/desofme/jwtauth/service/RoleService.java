package com.desofme.jwtauth.service;

import com.desofme.jwtauth.auth.Role;

import java.util.List;

public interface RoleService {
    Role saveRole(Role role);
    Role getRoleByName(String roleName);
}
