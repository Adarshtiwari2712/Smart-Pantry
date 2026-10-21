package com.smartpantry.service;

import com.smartpantry.dto.ApiResponse;
import com.smartpantry.dto.AuthRequest;
import com.smartpantry.model.User;
import com.smartpantry.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OtpService otpService;

    // Constructor injection: dono dependencies wire ho gayi
    public AuthService(UserRepository userRepository, OtpService otpService) {
        this.userRepository = userRepository;
        this.otpService = otpService;
    }

    // 1. Register User with OTP verification
    public ApiResponse<User> register(AuthRequest request) {
        // Step A: Pehle OTP verify karo
        boolean isValidOtp = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        if (!isValidOtp) {
            return ApiResponse.error("Galat ya expired OTP! Kripya dobara try karein.");
        }

        // Step B: Check duplicate phone number
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return ApiResponse.error("Yeh phone number already registered hai!");
        }

        // Step C: Save user in MongoDB
        User newUser = new User(request.getName(), request.getPhoneNumber());
        User savedUser = userRepository.save(newUser);
        return ApiResponse.success("Registration successful!", savedUser);
    }

    // 2. Login User with OTP verification
    public ApiResponse<User> login(AuthRequest request) {
        // Step A: Pehle check karo user exist karta hai ya nahi
        Optional<User> userOptional = userRepository.findByPhoneNumber(request.getPhoneNumber());
        if (userOptional.isEmpty()) {
            return ApiResponse.error("User nhi mila! Pehle register karein.");
        }

        // Step B: OTP verify karo
        boolean isValidOtp = otpService.verifyOtp(request.getPhoneNumber(), request.getOtp());
        if (!isValidOtp) {
            return ApiResponse.error("Galat ya expired OTP! Kripya dobara try karein.");
        }

        return ApiResponse.success("Login successful!", userOptional.get());
    }
}