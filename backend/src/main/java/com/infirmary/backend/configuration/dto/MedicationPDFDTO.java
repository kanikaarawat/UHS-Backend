package com.infirmary.backend.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicationPDFDTO {
    private String name;
    private Float dosageMorning;
    private Float dosageAfternoon;
    private Float dosageEvening;
    private Integer duration;
    private String suggestion;
}
