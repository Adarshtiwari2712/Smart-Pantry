package com.smartpantry.controller;

import com.smartpantry.dto.ApiResponse;
import com.smartpantry.dto.AuthRequest;
import com.smartpantry.model.User;
import com.smartpantry.service.AuthService;
import com.smartpantry.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.smartpantry.service.EmailService;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Agar frontend aur backend different origins par chal rahe hon
public class AuthController {

    @Autowired
    private OtpService otpService;


    @Autowired
    private AuthService authService;


    @Autowired
    private EmailService emailService;
    // 1. Send OTP API: POST http://localhost:8084
    // 84/api/auth/send-otp
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        otpService.generateOtp(email);
        return ResponseEntity.ok("OTP sent successfully to " + email);
    }

    //2. verify OTP


    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String name) {

        // Step A: Verify the email OTP
        boolean isValid = otpService.verifyOtp(email, otp);

        if (!isValid) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired OTP");
        }

        // Step B: Register the user in MongoDB
        ApiResponse<User> result =
                authService.registerWithEmail(name, email);

        // Step C: Return the registration result
        if (result.isSuccess()) {
            return ResponseEntity.ok("Registration successful!");
        }

        return ResponseEntity.badRequest()
                .body(result.getMessage());
    }


    @PostMapping("/test-expiry-alert")
    public ResponseEntity<String> testExpiryAlert(@RequestParam String email, @RequestParam String item) {
        boolean sent = emailService.sendExpiryAlertEmail(
                email,
                item,
                java.time.LocalDate.now().plusDays(2).toString(),
                2
        );
        if (sent) {
            return ResponseEntity.ok("Expiry alert email sent successfully to " + email);
        } else {
            return ResponseEntity.internalServerError().body("Failed to send email");
        }
    }
    @PostMapping("/login-verify-otp")
    public ResponseEntity<String> loginVerifyOtp(
            @RequestParam String email,
            @RequestParam String otp) {

        // Step A: Verify email OTP
        boolean isValid = otpService.verifyOtp(email, otp);

        if (!isValid) {
            return ResponseEntity.badRequest()
                    .body("Invalid or expired OTP");
        }

        // Step B: Find existing user
        ApiResponse<User> result =
                authService.loginWithEmail(email);

        if (result.isSuccess()) {
            return ResponseEntity.ok("Login successful!");
        }

        return ResponseEntity.badRequest()
                .body(result.getMessage());
    }
}

