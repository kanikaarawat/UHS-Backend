package com.infirmary.backend.configuration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemStatsDTO {
    private long totalPatients;
    private long activeSessions;
    private int appointmentsToday;
}

