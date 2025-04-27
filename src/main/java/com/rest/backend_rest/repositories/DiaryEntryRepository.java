package com.rest.backend_rest.repositories;

import com.rest.backend_rest.models.DiaryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryEntryRepository extends JpaRepository<DiaryEntry, Integer> {
    // You can add custom queries if needed
}
