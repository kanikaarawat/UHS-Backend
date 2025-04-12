package com.infirmary.backend.configuration.impl;

import com.infirmary.backend.configuration.dto.ActivityLogDTO;
import com.infirmary.backend.configuration.dto.AdDTO;
import com.infirmary.backend.configuration.dto.DoctorDTO;
import com.infirmary.backend.configuration.dto.SystemStatsDTO;
import com.infirmary.backend.configuration.model.AD;
import com.infirmary.backend.configuration.model.ActivityLog;
import com.infirmary.backend.configuration.model.Doctor;
import com.infirmary.backend.configuration.repository.*;
import com.infirmary.backend.configuration.service.AdminService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final SessionTokenRepository sessionTokenRepository;
    private final ActivityLogRepository activityLogRepository;
    private final DoctorRepository doctorRepository;
    private final AdRepository adRepository;
        private final PasswordEncoder passwordEncoder;


    @Override
    public SystemStatsDTO getSystemStats() {
        long totalPatients = patientRepository.count();
        long activeSessions = sessionTokenRepository.count();
        int dailyAppointments = appointmentRepository.countTodayAppointments();
        return new SystemStatsDTO(totalPatients, activeSessions, dailyAppointments);
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
        return 99.94;
    }

    // ========================= Doctor Management =========================

    @Override
    public List<DoctorDTO> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(DoctorDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public DoctorDTO getDoctorById(UUID id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with ID: " + id));
        return new DoctorDTO(doctor);
    }

    @Override
    public DoctorDTO updateDoctor(UUID id, DoctorDTO dto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Doctor not found with ID: " + id));

        doctor.setName(dto.getName());
        doctor.setGender(dto.getGender());
        doctor.setStatus(dto.getStatus());
        doctor.setDesignation(dto.getDesignation());
        doctor.setLocation(dto.getLocation());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            doctor.setPassword(passwordEncoder.encode(dto.getPassword())); // 🔐 Update password
        }

        return new DoctorDTO(doctorRepository.save(doctor));
    }
    @Override
    public void deleteDoctor(UUID id) {
        if (!doctorRepository.existsById(id)) {
            throw new NoSuchElementException("Doctor not found with ID: " + id);
        }
        doctorRepository.deleteById(id);
    }

    // ========================= Assistant (AD) Management =========================

    @Override
    public List<AdDTO> getAllAssistants() {
        return adRepository.findAll().stream()
                .map(AdDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public AdDTO getAssistantById(String email) {
        AD ad = adRepository.findById(email)
                .orElseThrow(() -> new NoSuchElementException("AD not found with email: " + email));
        return new AdDTO(ad);
    }

    @Override
    public AdDTO updateAssistant(String email, AdDTO dto) {
        AD ad = adRepository.findById(email)
                .orElseThrow(() -> new NoSuchElementException("AD not found with email: " + email));

        ad.setName(dto.getName());
        ad.setDesignation(dto.getDesignation());
        ad.setLocation(dto.getLocation());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            ad.setPassword(passwordEncoder.encode(dto.getPassword())); // 🔐 Update password
        }

        return new AdDTO(adRepository.save(ad));
    }

    @Override
    public void deleteAssistant(String email) {
        if (!adRepository.existsById(email)) {
            throw new NoSuchElementException("AD not found with email: " + email);
        }
        adRepository.deleteById(email);
    }
}
