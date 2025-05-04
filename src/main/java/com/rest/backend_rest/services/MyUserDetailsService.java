package com.rest.backend_rest.services;

import com.rest.backend_rest.dtos.UserDTO;
import com.rest.backend_rest.dtos.UserProfileDTO;
import com.rest.backend_rest.exceptions.IllegalArgument;
import com.rest.backend_rest.models.UserPrincipal;
import com.rest.backend_rest.models.Users;
import com.rest.backend_rest.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public boolean deleteUserByEmail(String email) {
        Optional<Users> user = Optional.ofNullable(userRepo.findByEmail(email));
        if (user.isPresent()) {
            userRepo.delete(user.get());
            return true;
        }
        return false;
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

    public UserProfileDTO updateUser(String token, Users updatedUser) {
        try {
            UserDTO userDTO = getUserDetails(token);

            // ✅ Fetch the existing user from DB using email
            Users currentUser = userRepo.findByEmail(userDTO.getEmail());
            if (currentUser == null) {
                return null;
            }

            // 🔁 Update only non-null fields
            if (updatedUser.getFirstName() != null) {
                currentUser.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                currentUser.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(currentUser.getEmail())) {
                // Optional: check if new email already exists (to avoid unique constraint violation)
                Users existing = userRepo.findByEmail(updatedUser.getEmail());
                if (existing != null) {
                    throw new IllegalArgument("Email '" + updatedUser.getEmail() + "' is already taken.");
                }
                currentUser.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getContactNumber() != null) {
                currentUser.setContactNumber(updatedUser.getContactNumber());
            }
            if (updatedUser.getEmergencyContactNumber() != null) {
                currentUser.setEmergencyContactNumber(updatedUser.getEmergencyContactNumber());
            }
            if (updatedUser.getEmergencyContactName() != null) {
                currentUser.setEmergencyContactName(updatedUser.getEmergencyContactName());
            }
            if (updatedUser.getDateOfBirth() != null) {
                currentUser.setDateOfBirth(updatedUser.getDateOfBirth());
            }
            if (updatedUser.getCurrentMedication() != null) {
                currentUser.setCurrentMedication(updatedUser.getCurrentMedication());
            }
            if (updatedUser.getAlcoholOrDrugUse() != null) {
                currentUser.setAlcoholOrDrugUse(updatedUser.getAlcoholOrDrugUse());
            }

            // ✅ Save the updated entity
            userRepo.save(currentUser);

            return new UserProfileDTO(currentUser);
        } catch (Exception e) {
            throw new RuntimeException("Error updating user details.");
        }
    }

    public Users findByEmail(String email) {
        return userRepo.findByEmail(email);
    }
}