package com.rest.backend_rest.dtos;

import com.rest.backend_rest.models.Users;
import lombok.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDTO {

    private String firstName;

    private String lastName;

    private String email;

    private String contactNumber;

    private String emergencyContactNumber;

    private String emergencyContactName;

    private LocalDate dateOfBirth;

    private String currentMedication;

    private String alcoholOrDrugUse;

    public UserProfileDTO(Users user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.contactNumber = user.getContactNumber();
        this.emergencyContactNumber = user.getEmergencyContactNumber();
        this.emergencyContactName = user.getEmergencyContactName();
        this.dateOfBirth = user.getDateOfBirth();
        this.currentMedication = user.getCurrentMedication();
        this.alcoholOrDrugUse = user.getAlcoholOrDrugUse();
    }
}