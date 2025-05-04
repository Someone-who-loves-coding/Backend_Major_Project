package com.rest.backend_rest.repositories;

import com.rest.backend_rest.models.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Integer> {
    // You can add custom queries if needed
    // To Get all entries by user email
    @Query("SELECT d FROM diary_entries d WHERE d.user.email = :user_email")
    List<DiaryEntry> findByUserEmail(@Param("user_email") String email);

    // Get a specific entry by user and date (exact match on date only)
    @Query("SELECT d FROM diary_entries d WHERE d.user.email = :user_email AND FUNCTION('DATE', d.timeEntry) = :timeEntry")
    Optional<DiaryEntry> findByUserEmailAndDate(@Param("user_email") String email, @Param("timeEntry") LocalDateTime date);

}
