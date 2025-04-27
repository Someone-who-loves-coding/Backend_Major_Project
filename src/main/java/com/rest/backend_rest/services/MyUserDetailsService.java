package com.rest.backend_rest.services;

import com.rest.backend_rest.dtos.UserDTO;
import com.rest.backend_rest.models.UserPrincipal;
import com.rest.backend_rest.models.Users;
import com.rest.backend_rest.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {


    private final UserRepo userRepo;

    private final JWTService jwtTokenUtil;
    @Autowired
    public MyUserDetailsService(UserRepo userRepo, JWTService jwtTokenUtil) {
        this.userRepo = userRepo;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users user = userRepo.findByEmail(email);
        if (user == null) {
            System.out.println("User Not Found");
            throw new UsernameNotFoundException("user not found");
        }
        
        return new UserPrincipal(user);
    }

    public UserDTO getUserDetails(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Authorization header is missing.");
        }

        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        String email = jwtTokenUtil.extractUserName(jwtToken);
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Invalid JWT Token.");
        }

        Users user = userRepo.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("User not found.");
        }

        return new UserDTO(user);
    }
}