package com.desofme.jwtauth.repository;

import com.desofme.jwtauth.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email=:username and u.status=:status")
    Optional<User> findByUsernameAndStatus(String username, Integer status);
    @Query("select u from User u where u.email=:username")
    Optional<User> findByUsername(String username);

    List<User> findUserByStatus(Integer status);
}
