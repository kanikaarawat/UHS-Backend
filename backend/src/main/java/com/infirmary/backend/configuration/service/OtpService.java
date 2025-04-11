package com.infirmary.backend.configuration.service;

public interface OtpService {
    void generateAndSendOtp(String email, String mobile); // this should match
    String verifyOtp(String emailOrMobile, String otp);    // return String (JWT), not boolean
}
