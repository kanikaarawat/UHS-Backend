package com.infirmary.backend.configuration.impl;

import com.infirmary.backend.configuration.dto.ActivityLogDTO;
import com.infirmary.backend.configuration.dto.SystemStatsDTO;
import com.infirmary.backend.configuration.model.ActivityLog;
import com.infirmary.backend.configuration.repository.*;
import com.infirmary.backend.configuration.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    // private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final ActivityLogRepository activityLogRepository;

    @Override
    public SystemStatsDTO getSystemStats() {
        long totalPatients = patientRepository.count();
        long activeSessions = sessionTokenRepository.count();
        int dailyAppointments = appointmentRepository.countTodayAppointments();
    
        return new SystemStatsDTO(
            totalPatients,
            activeSessions,
            dailyAppointments
        );
    }
    
    @Override
    public long getTotalPatients() {
        return patientRepository.count();
    }

    @Override
    public long getActiveSessions() {
        return sessionTokenRepository.count();
    }

    @Override
    public List<ActivityLogDTO> getRecentActivities() {
        List<ActivityLog> logs = activityLogRepository.findTop10ByOrderByTimestampDesc();
        return logs.stream().map(ActivityLogDTO::new).collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        health.put("database", "Up ✅");
        health.put("backend", "Running 🚀");
        health.put("uptime", getSystemUptime() + "%");
        health.put("load", "Low");
        return health;
    }

    @Override
    public List<String> getCurrentAlerts() {
        List<String> alerts = new ArrayList<>();
        if (appointmentRepository.countByStatus("PENDING") > 100) {
            alerts.add("⚠ High pending appointments.");
        }
        if (sessionTokenRepository.count() > 300) {
            alerts.add("⚠ Too many active sessions.");
        }
        return alerts;
    }


    private double getSystemUptime() {
        return 99.94; // Stub: replace with uptime monitor later
    }
}
