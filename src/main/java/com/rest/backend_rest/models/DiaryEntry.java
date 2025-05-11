package com.rest.backend_rest.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

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

//    @ElementCollection(fetch = FetchType.EAGER)
//    private Map<String, Double> emotions;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "diary_entry_emotions", joinColumns = @JoinColumn(name = "diary_entry_id"))
    @MapKeyColumn(name = "emotion_type")
    @Column(name = "emotion_value")
    private Map<String, Double> emotions;

    private LocalDateTime timeEntry; // Time when the entry was created
}
