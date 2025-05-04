package com.rest.backend_rest.models;

import com.rest.backend_rest.dtos.UserDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String firstName; // e.g., Prakhar

    @Column(nullable = false)
    private String lastName; // e.g., Saxena

    @Column(nullable = false, unique = true)
    private String email; // e.g., prakhar12saxenas@gmail.com

    @Column(nullable = false)
    private String password;

    private LocalDateTime lastEntryTime;

    // Additional fields
    private String contactNumber;

    private String emergencyContactNumber;

    private String emergencyContactName;

    private LocalDate dateOfBirth; // Format: yyyy-MM-dd

    @Column(length = 1000)
    private String currentMedication;

    @Column(length = 1000)
    private String alcoholOrDrugUse; // e.g., frequency details

    public Users(UserDTO userDTO) {
        this.firstName = userDTO.getFirstName();
        this.lastName = userDTO.getLastName();
        this.email = userDTO.getEmail();
    }
}
