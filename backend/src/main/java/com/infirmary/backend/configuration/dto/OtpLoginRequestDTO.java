package com.infirmary.backend.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpLoginRequestDTO {
    private String email;
    private String phoneNumber;
}
