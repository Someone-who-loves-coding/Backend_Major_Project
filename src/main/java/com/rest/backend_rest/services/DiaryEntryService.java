package com.rest.backend_rest.services;

import com.rest.backend_rest.dtos.DiaryEntryDTO;
import com.rest.backend_rest.exceptions.UserNotFoundException;
import com.rest.backend_rest.models.DiaryEntry;
import com.rest.backend_rest.models.Users;
import com.rest.backend_rest.repositories.DiaryEntryRepository;
import com.rest.backend_rest.repositories.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DiaryEntryService {

    private final DiaryEntryRepository diaryEntryRepository;

    private final UserRepo usersRepository;

    private final JWTService jwtTokenUtil;  // Inject JwtTokenUtil

    private final EmotionAnalysisService emotionService;

    @Autowired
    public DiaryEntryService(EmotionAnalysisService emotionService, DiaryEntryRepository diaryEntryRepository, UserRepo usersRepository, JWTService jwtTokenUtil) {
        this.emotionService = emotionService;
        this.diaryEntryRepository = diaryEntryRepository;
        this.usersRepository = usersRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public DiaryEntry addDiaryEntry(String token, String heading, String entry) {
        // Extract email from the JWT token
        String email = jwtTokenUtil.extractUserName(token);

        // Fetch the user based on the extracted email
        Users user = usersRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // Create and save the diary entry
        DiaryEntry diaryEntry = new DiaryEntry();
        diaryEntry.setUser(user);
        diaryEntry.setHeading(heading);
        diaryEntry.setEntry(entry);
        diaryEntry.setTimeEntry(LocalDateTime.now()); // Automatically set the current time
//        diaryEntry.setTimeEntry(time);

        Map<String, Double> emotions = emotionService.analyze(entry);
        diaryEntry.setEmotions(emotions);
        log.info("Emotions: {}", diaryEntry.getEmotions());
        DiaryEntry savedEntry = diaryEntryRepository.save(diaryEntry);

        // Update the user's lastEntryTime
        user.setLastEntryTime(savedEntry.getTimeEntry());
        usersRepository.save(user); // Save the updated user back to DB

        return savedEntry;
    }

    public List<String> getEntryDates(String token) {

        String email = jwtTokenUtil.extractUserName(token);
        List<DiaryEntry> entries = diaryEntryRepository.findByUserEmail(email);

        return entries.stream()
                .map(e -> e.getTimeEntry().toLocalDate().toString()) // yyyy-MM-dd
                .distinct()
                .collect(Collectors.toList());
    }

    public List<DiaryEntry> getEntryByDate(String token, String date) {// DTO
        String userEmail = jwtTokenUtil.extractUserName(token);
        LocalDate targetDate = LocalDate.parse(date); // "yyyy-MM-dd"

        List<DiaryEntry> entry = diaryEntryRepository.findByUserEmail(userEmail);
        List<DiaryEntry> foundEntries = new ArrayList<>();  // DTO
        for(DiaryEntry e : entry) {
            if(e.getTimeEntry().toLocalDate().equals(targetDate))
                foundEntries.add(e); // DiaryEntryDTO
            log.info("Found entry: {}", e.getEmotions());
        }


        return foundEntries;
    }

    public void saveOrUpdate(String token, String date) {
        String userEmail = jwtTokenUtil.extractUserName(token);
        LocalDate targetDate = LocalDate.parse(date);

        List<DiaryEntry> entries = diaryEntryRepository.findByUserEmail(userEmail);
        for(DiaryEntry e : entries) {
            if(e.getTimeEntry().toLocalDate().equals(targetDate)){
                Map<String, Double> emotions = emotionService.analyze(e.getEntry());
                e.setEmotions(emotions);
                diaryEntryRepository.save(e);
            }
        }
    }
}
