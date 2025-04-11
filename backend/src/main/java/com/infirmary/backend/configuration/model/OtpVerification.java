package com.infirmary.backend.configuration.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;        // or phone number if preferred
    private String mobile;       // optional: for mobile-based OTP
    private String otp;
    private boolean verified;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
