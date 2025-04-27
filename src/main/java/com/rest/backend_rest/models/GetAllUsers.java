package com.rest.backend_rest.models;

import com.fasterxml.jackson.annotation.JsonFilter;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@JsonFilter("UserFilter")
public class GetAllUsers {
    List<Users> users;
}
