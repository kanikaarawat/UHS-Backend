package com.infirmary.backend.configuration.repository;

import com.infirmary.backend.configuration.model.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByEmailOrderByCreatedAtDesc(String email);
    Optional<OtpVerification> findTopByMobileOrderByCreatedAtDesc(String mobile);
}
