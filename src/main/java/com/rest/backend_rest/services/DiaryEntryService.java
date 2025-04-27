package com.rest.backend_rest.services;

import com.rest.backend_rest.exceptions.UserNotFoundException;
import com.rest.backend_rest.models.DiaryEntry;
import com.rest.backend_rest.models.Users;
import com.rest.backend_rest.repositories.DiaryEntryRepository;
import com.rest.backend_rest.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DiaryEntryService {

    @Autowired
    private DiaryEntryRepository diaryEntryRepository;

    @Autowired
    private UserRepo usersRepository;

    @Autowired
    private JWTService jwtTokenUtil;  // Inject JwtTokenUtil

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
        DiaryEntry savedEntry = diaryEntryRepository.save(diaryEntry);

        // Update the user's lastEntryTime
        user.setLastEntryTime(savedEntry.getTimeEntry());
        usersRepository.save(user); // Save the updated user back to DB

        return savedEntry;
    }
}
