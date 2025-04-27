//package com.rest.backend_rest.controller;
//
//import com.rest.backend_rest.models.Users;
//import com.rest.backend_rest.services.AuthService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.media.Content;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/v1/auth")
//public class AuthController {
//
//    private final AuthService authService;
//
//    public AuthController(AuthService authService) {
//        this.authService = authService;
//    }
//
//    @PostMapping("/signup")
//    @Operation(
//            summary = "Create User",
//            description = "Creates and saves the details of the given user."
//    )
//    @ApiResponses(value = {
//            @ApiResponse(
//                    responseCode = "201",
//                    description = "Successfully created and saved the details of the specified user.",
//                    content = @Content(schema = @Schema(implementation = Users.class))
//            ),
//            @ApiResponse(
//                    responseCode = "401",
//                    description = "Unauthorized Access"
//            )
//    })
//    public ResponseEntity<String> signup(@RequestBody Users user) {
//        authService.registerUser(user);
//        return new ResponseEntity<>("User registered successfully", HttpStatus.CREATED);
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestBody Users user, HttpServletResponse response) {
//        if (authService.authenticate(user.getEmail(), user.getPassword())) {
//
//            // 🍪 Send token as HttpOnly secure cookie
//            Cookie cookie = new Cookie("authToken", user.getEmail());
//            cookie.setHttpOnly(true);
//            cookie.setSecure(true);
//            cookie.setPath("/");
//            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
//            response.addCookie(cookie);
//
//            return new ResponseEntity<>("Login successful", HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);
//        }
//    }
//}
