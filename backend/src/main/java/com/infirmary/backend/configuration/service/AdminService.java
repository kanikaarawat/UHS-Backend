package com.infirmary.backend.configuration.service;

import com.infirmary.backend.configuration.dto.ActivityLogDTO;
import com.infirmary.backend.configuration.dto.AdDTO;
import com.infirmary.backend.configuration.dto.DoctorDTO;
import com.infirmary.backend.configuration.dto.SystemStatsDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AdminService {
    SystemStatsDTO getSystemStats();
    long getTotalPatients();
    long getActiveSessions();
    List<ActivityLogDTO> getRecentActivities();
    Map<String, Object> getSystemHealth();
    List<String> getCurrentAlerts();

    // Doctor management
    List<DoctorDTO> getAllDoctors();
    DoctorDTO getDoctorById(UUID id);
    DoctorDTO updateDoctor(UUID id, DoctorDTO dto);
    void deleteDoctor(UUID id);

    // AD (Nursing Assistant) management
    List<AdDTO> getAllAssistants();
    AdDTO getAssistantById(String email);
    AdDTO updateAssistant(String email, AdDTO dto);
    void deleteAssistant(String email);
}
