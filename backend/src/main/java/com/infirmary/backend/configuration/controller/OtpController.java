package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.service.OtpService;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpSendRequest request) {
        otpService.generateAndSendOtp(request.getEmail(), request.getMobile());
        return ResponseEntity.ok("OTP sent successfully.");
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request) {
        try {
            String jwtToken = otpService.verifyOtp(request.getEmailOrMobile(), request.getOtp());
            return ResponseEntity.ok().body(java.util.Map.of("token", jwtToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid or expired OTP: " + e.getMessage());
        }
    }
    

    @Data
    static class OtpSendRequest {
        private String email;
        private String mobile;
    }

    @Data
    static class OtpVerifyRequest {
        private String emailOrMobile;
        private String otp;
    }
}
