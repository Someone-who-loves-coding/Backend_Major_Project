package com.rest.backend_rest.repositories;

import com.rest.backend_rest.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<Users, Long> {
    Users findByEmail(String email);
}
