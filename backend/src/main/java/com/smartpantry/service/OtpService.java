package com.smartpantry.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.*;

import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired
    private EmailService emailService;

    //Email -> OTP mapping in memory (ya redis)
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    // 6-digit random OTP generator
    public String generateOtp(String email) {
        SecureRandom random = new SecureRandom();
        int otpNum = 100000 + random.nextInt(900000);
        String otp = String.valueOf(otpNum);

        //Save OTP
        otpStorage.put(email, otp);

        // Send OTP via Email
        boolean sent = emailService.sendOtpEmail(email, otp);
        if (!sent) {
            System.err.println("OTP Email delivery failed for: " + email);
        }
        return otp;
    }

    // OTP Verification
    public boolean verifyOtp(String email, String enteredOtp) {
        if (otpStorage.containsKey(email)) {
            String storedOtp = otpStorage.get(email);
            if (storedOtp.equals(enteredOtp)) {
                otpStorage.remove(email); // Verified, remove OTP
                return true;
            }
        }
        return false;
    }

}


