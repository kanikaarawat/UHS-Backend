package com.infirmary.backend.configuration.dto;

import lombok.*;

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

    private String phoneNumber; // ✅ Add this
    private String bloodGroup;  // ✅ Add this
    private String school;      // ✅ Add this
}
