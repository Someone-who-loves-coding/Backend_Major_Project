package com.rest.backend_rest.services;

import com.rest.backend_rest.exceptions.UserAlreadyExists;
import com.rest.backend_rest.models.Users;
import com.rest.backend_rest.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    private final JWTService jwtService;

    private final AuthenticationManager authManager;

    private final UserRepo repo;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    private final MyUserDetailsService userDetailsService;

    @Autowired
    public UserServiceImpl(UserRepo repo, JWTService jwtService, AuthenticationManager authManager, MyUserDetailsService userDetailsService) {
        this.repo = repo;
        this.jwtService = jwtService;
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
    }

    public void logout(String token) {
        // Blacklist the JWT token
        jwtService.blacklistToken(token);
    }

    public boolean validateToken(String token) {
        UserDetails user = userDetailsService.loadUserByUsername(jwtService.extractUserName(token));
        return jwtService.validateToken(token, user);
    }

    public Users register(Users user) {
        if (repo.findByEmail(user.getEmail()) != null) {
            throw new UserAlreadyExists("Email '" + user.getEmail() + "' is already taken.");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return repo.save(user);
    }

    public String loginUser(Users user) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(user.getEmail());
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
    
    
}
