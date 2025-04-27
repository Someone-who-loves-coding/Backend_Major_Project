package com.rest.backend_rest.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.Date;

@Entity
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BlacklistedToken {

    @Id
    private String token;

    private Date createdAt;

    public BlacklistedToken(String token) {
        this.token = token;
        this.createdAt = new Date();
    }
}
