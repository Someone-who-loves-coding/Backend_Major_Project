package com.rest.backend_rest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "diary_entries")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DiaryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Unique ID for each diary entry
    
    @ManyToOne
    @JoinColumn(name = "user_email", referencedColumnName = "email")
    private Users user; // Foreign key reference to Users table

    private String heading; // Optional heading
    private String entry; // Paragraph for diary entry


    private LocalDateTime timeEntry; // Time when the entry was created
}
