package com.infirmary.backend.configuration.impl;

import com.infirmary.backend.configuration.jwt.JwtUtils;
import com.infirmary.backend.configuration.model.OtpVerification;
import com.infirmary.backend.configuration.repository.OtpVerificationRepository;
import com.infirmary.backend.configuration.service.OtpService;
import com.infirmary.backend.shared.utility.MailService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepo;
    private final MailService mailService;
    private final JwtUtils jwtUtils;

    @Override
    public void generateAndSendOtp(String email, String mobile) {
        String otp = generateOtp();

        OtpVerification otpEntity = OtpVerification.builder()
                .email(email)
                .mobile(mobile)
                .otp(otp)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepo.save(otpEntity);

        try {
            mailService.sendOtpMail(email, otp); // ✅ Sends actual email
        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    @Override
    public String verifyOtp(String emailOrMobile, String otp) {
        return otpRepo.findTopByEmailOrderByCreatedAtDesc(emailOrMobile)
                .or(() -> otpRepo.findTopByMobileOrderByCreatedAtDesc(emailOrMobile))
                .filter(entry -> entry.getOtp().equals(otp) && entry.getExpiresAt().isAfter(LocalDateTime.now()))
                .map(entry -> generateJwtToken(entry.getEmail()))
                .orElseThrow(() -> new RuntimeException("Invalid or expired OTP"));
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
    }

    private String generateJwtToken(String email) {
        // Assign default role as ROLE_PATIENT
        User user = new User(email, "", 
            java.util.List.of(new SimpleGrantedAuthority("ROLE_PATIENT")));
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        return jwtUtils.genrateJwtToken(auth);
    }
}
