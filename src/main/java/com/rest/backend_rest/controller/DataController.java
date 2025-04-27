package com.rest.backend_rest.controller;

import com.rest.backend_rest.dtos.UserDTO;
import com.rest.backend_rest.models.DiaryEntry;
import com.rest.backend_rest.models.DiaryEntryRequest;
import com.rest.backend_rest.models.Users;
import com.rest.backend_rest.services.DiaryEntryService;
import com.rest.backend_rest.services.JWTService;
import com.rest.backend_rest.services.MyUserDetailsService;
import com.rest.backend_rest.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api/v1")
@Tag(name = "Users Service APIs", description = "Rest Service for users.")
public class DataController {

    private final DiaryEntryService diaryEntryService;
    private final JWTService jwtTokenUtil;
    private final MyUserDetailsService userService;

    @Autowired
    public DataController(DiaryEntryService diaryEntryService, JWTService jwtTokenUtil, MyUserDetailsService userService) {
        this.diaryEntryService = diaryEntryService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @PostMapping("/add")
    public DiaryEntry addDiaryEntry(@RequestHeader("Authorization") String token,
                                    @RequestBody DiaryEntryRequest diaryEntryRequest) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return diaryEntryService.addDiaryEntry(jwtToken, diaryEntryRequest.getHeading(), diaryEntryRequest.getEntry());
    }
}
