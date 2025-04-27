package com.rest.backend_rest.repositories;

import com.rest.backend_rest.models.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, String> {

    // Find a blacklisted token by its value
    boolean existsByToken(String token);
}
