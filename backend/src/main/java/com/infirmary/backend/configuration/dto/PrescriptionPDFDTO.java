package com.infirmary.backend.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionPDFDTO {
    private String name;
    private String id;
    private LocalDate dateOfBirth;
    private String school;
    private String date;
    private String time;
    private String designation;
    private String residenceType;
    private String sex;
    private List<MedicationPDFDTO> meds;
    private String diagnosis;
    private String dietaryRemarks;
    private String testNeeded;
    private String doctorName;
}
