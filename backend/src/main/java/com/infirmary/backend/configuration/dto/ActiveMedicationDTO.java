package com.infirmary.backend.configuration.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class ActiveMedicationDTO {
    private UUID pres_medicine_id;
    private String medicineName;
    private String dosage;
    private int duration;
    private String appointmentDate;
    private String endDate;
}
