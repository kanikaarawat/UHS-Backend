package com.infirmary.backend.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpVerificationRequestDTO {
    private String email;
    private String phoneNumber;
    private String otp;
}
