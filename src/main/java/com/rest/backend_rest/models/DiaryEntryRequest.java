package com.rest.backend_rest.models;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DiaryEntryRequest {
    private String heading;
    private String entry;

    // getters and setters
}
