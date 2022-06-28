package com.desofme.jwtauth;

import com.desofme.jwtauth.auth.Role;
import com.desofme.jwtauth.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@RequiredArgsConstructor
public class JwtAuthApplication implements CommandLineRunner{

    private final RoleService roleService;

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthApplication.class, args);
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        roleService.saveRole(new Role("USER"));
        roleService.saveRole(new Role("ADMIN"));
    }
}
