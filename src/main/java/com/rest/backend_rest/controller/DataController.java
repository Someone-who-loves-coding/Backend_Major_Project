package com.rest.backend_rest.controller;

import com.rest.backend_rest.dtos.DiaryEntryDTO;
import com.rest.backend_rest.dtos.UserDTO;
import com.rest.backend_rest.dtos.UserProfileDTO;
import com.rest.backend_rest.models.DiaryEntry;
import com.rest.backend_rest.models.DiaryEntryRequest;
import com.rest.backend_rest.models.UserPrincipal;
import com.rest.backend_rest.models.Users;
import com.rest.backend_rest.repositories.DiaryEntryRepository;
import com.rest.backend_rest.services.DiaryEntryService;
import com.rest.backend_rest.services.JWTService;
import com.rest.backend_rest.services.MyUserDetailsService;
import com.rest.backend_rest.services.ReportAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Users Service APIs", description = "Rest Service for users.")
public class DataController {

    private final DiaryEntryService diaryEntryService;
    private final MyUserDetailsService userService;

    @Autowired
    public DataController(DiaryEntryService diaryEntryService, MyUserDetailsService userService, DiaryEntryRepository repo, ReportAnalysisService reportAnalysisService, JWTService jw) {
        this.diaryEntryService = diaryEntryService;
        this.userService = userService;
        this.repo = repo;
        this.reportAnalysisService = reportAnalysisService;
        this.jw = jw;
    }

    @GetMapping("/details")
    @Operation(
            summary = "Get User",
            description = "Retrieves the details of the specified user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the details of the specified user.",
                    content = @Content(schema = @Schema(implementation = Users.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized Access"
            )
    })
    public ResponseEntity<?> getUserDetails(@RequestHeader("Authorization") String token) {
        try {
            UserDTO userDetails = userService.getUserDetails(token);
            return ResponseEntity.ok(userDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/userprofile")
    @Operation(
            summary = "Get User Profile",
            description = "Retrieves the profile of the specified user."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved the details of the specified user.",
                    content = @Content(schema = @Schema(implementation = Users.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized Access"
            )
    })
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            UserDTO userDTO = userService.getUserDetails(token);

            // ✅ Fetch the existing user from DB using email
            Users currentUser = userService.findByEmail(userDTO.getEmail());
            return ResponseEntity.ok(new UserProfileDTO(currentUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    // PATCH endpoint to update user details
    @Operation(
            summary = "Update User Details",
            description = "Updates specific user details such as contact information, medication, etc."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully updated user details",
                    content = @Content(schema = @Schema(implementation = Users.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized Access"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User Not Found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request"
            )
    })
    @PatchMapping("/update")
    public ResponseEntity<?> updateUserDetails(@RequestHeader("Authorization") String token,
                                               @RequestBody Users updatedUser) {
        System.out.println("Updating user details...");
        return ResponseEntity.ok(userService.updateUser(token, updatedUser));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete User", description = "Deletes the currently authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "404", description = "User not found.")
    })
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token) {
        try {
            UserDTO userDTO = userService.getUserDetails(token);
            boolean deleted = userService.deleteUserByEmail(userDTO.getEmail());

            if (deleted) {
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found or could not be deleted.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/add")
    public DiaryEntry addDiaryEntry(@RequestHeader("Authorization") String token,
                                    @RequestBody DiaryEntryRequest diaryEntryRequest) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return diaryEntryService.addDiaryEntry(jwtToken, diaryEntryRequest.getHeading(), diaryEntryRequest.getEntry());
    }

    @GetMapping("/entry-dates")
    public List<String> getEntryDates(@RequestHeader("Authorization") String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return diaryEntryService.getEntryDates(jwtToken);
    }

    @GetMapping("/entry")
    public ResponseEntity<?> getEntryByDate(
            @RequestParam String date,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return ResponseEntity.ok(diaryEntryService.getEntryByDate(jwtToken, date));
    }

    @PostMapping("/entry")
    public ResponseEntity<?> saveOrUpdateEntry(
            @RequestParam String date,
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        diaryEntryService.saveOrUpdate(jwtToken, date);
        return ResponseEntity.ok("Saved");
    }

    private final DiaryEntryRepository repo;
    private final ReportAnalysisService reportAnalysisService;
    private final JWTService jw;

    @GetMapping("/final")
    public ResponseEntity<?> generateFinalReport(@RequestHeader("Authorization") String token) {
        // Extract email from token
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;  // Assuming "Bearer <token>"
        log.info("email {}", jwtToken);
        // Load the user using the email from the token
        String username = jw.extractUserName(jwtToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        log.warn("User details: {}", userDetails.getUsername());

        // Ensure the principal is of the correct type
        if (!(userDetails instanceof UserPrincipal principal)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid user principal."));
        }

        // Get the Users entity from the principal
        Users user = principal.getUser();  // Assuming `getUser()` returns the Users entity

        // Retrieve diary entries for the user
        List<DiaryEntry> entries = repo.findByUserOrderByTimeEntryAsc(user);

        // Ensure the user has at least 30 entries
        if (entries.size() < 30) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Final report requires at least 30 diary entries.","entries", entries.size()));
        }

        // Generate the final report
        String report = reportAnalysisService.generateFinalReport(entries);

        // Return the generated report
        return ResponseEntity.ok(Map.of("report", report));
    }
}
