package com.desofme.jwtauth.repository;

import com.desofme.jwtauth.auth.ConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepo extends JpaRepository<ConfirmationToken, Long> {

    @Query("select t from ConfirmationToken t where t.token=:token")
    Optional<ConfirmationToken> findByToken(String token);

}
