package com.infirmary.backend.configuration.dto;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String email;
    private String name;
    private String role;
    private String status;
    private String lastLogin;
    private String password;
    
    private String sapId;
    private String phoneNumber;
    private String bloodGroup;
    private String school;

    // ✅ Add these fields
    private LocalDate dateOfBirth;
    private String program;
    private String emergencyContact;
    private String gender;
}
