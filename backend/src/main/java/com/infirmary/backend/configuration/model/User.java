package com.infirmary.backend.configuration.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "sap_email", nullable = false)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "gender")
    private String gender;

    @Column(name = "school")
    private String school;

    @Column(name = "program")
    private String program;

    @Column(name = "sap_id")
    private String sapId;

    @Column(name = "date_of_birth")
    private java.sql.Date dob;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "image_url")
    private String imageUrl;
}
