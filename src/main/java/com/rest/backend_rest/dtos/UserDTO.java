package com.rest.backend_rest.dtos;

import com.rest.backend_rest.models.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime lastEntryTime;

    public UserDTO(Users user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.lastEntryTime = user.getLastEntryTime();
    }
}
