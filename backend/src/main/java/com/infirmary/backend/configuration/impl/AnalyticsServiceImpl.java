package com.infirmary.backend.configuration.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import com.infirmary.backend.configuration.model.Doctor;
import com.infirmary.backend.configuration.repository.AppointmentRepository;
import com.infirmary.backend.configuration.repository.DoctorRepository;
import com.infirmary.backend.configuration.repository.PrescriptionMedsRepository;
import com.infirmary.backend.configuration.service.AnalyticsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService{
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionMedsRepository prescriptionMedsRepository;
    private final DoctorRepository doctorRepository;
    
    @Override
    public Long getAllPatient() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                log.error("Authentication object is null.");
                throw new RuntimeException("Unauthorized access.");
            }
    
            String username = auth.getName();
            log.info("Authenticated user trying to access getAllPatient(): {}", username);
    
            Optional<Doctor> doctorOpt = doctorRepository.findByDoctorEmail(username);
            log.info("Doctor lookup for email {} returned present? {}", username, doctorOpt.isPresent());
    
            if (doctorOpt.isPresent()) {
                UUID doctorId = doctorOpt.get().getDoctorId();
                LocalDate lastDate = LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(30);
                return appointmentRepository.countAppointmentsForDoctor(doctorId, lastDate);
            } else {
                // Check for admin role explicitly
                boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    
                if (isAdmin) {
                    return appointmentRepository.countDistinctPatients();
                } else {
                    log.error("Unauthorized role for analytics access: {}", auth.getAuthorities());
                    throw new RuntimeException("Unauthorized access to analytics.");
                }
            }
    
        } catch (Exception e) {
            log.error("Error in getAllPatient()", e);
            throw new RuntimeException("Error while retrieving total patient count.");
        }
    }
     
    @Override
    public List<Object[]> getPatientSchoolWise() {
        LocalDate lasDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        lasDate = lasDate.minusDays(30);
        return appointmentRepository.countAppointmentsGroupedBySchoolNative(lasDate);
    }

    @Override
    public List<Object[]> getTopTenMeds() {
        LocalDate lasDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        lasDate = lasDate.minusDays(30);
        
        return prescriptionMedsRepository.countPrescriptionMedsGroupByName(lasDate);
    }

    @Override
    public List<Object[]> getByResidenceType() {
        LocalDate lasDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        lasDate = lasDate.minusDays(30);

        return appointmentRepository.countAppointmentsGroupedByResidenceType(lasDate);
    }

    @Override
    public List<Map<?,?>> getByDoctor() {
        LocalDate lasDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        lasDate = lasDate.minusDays(30);

        List<Object[]> results = appointmentRepository.countAppointmentsGroupedByDoctor(lasDate);

        // Map doctor ID to name
        return results.stream().map(result -> {
            UUID doctorId = (UUID) result[0];
            long count = ((Number) result[1]).longValue();

            // Fetch doctor name
            String doctorName = doctorRepository.findById(doctorId)
                                 .map(Doctor::getName)
                                 .orElse("Unknown Doctor");

            // Build response map
            Map<String, Object> map = new HashMap<>();
            map.put("name", doctorName);
            map.put("patientCount", count);
            return map;
        }).collect(Collectors.toList());

    }

    @Override
    public List<?> getMonthlyData() {
        return appointmentRepository.countAppointmentByMonth();
    }

    @Override
    public List<Object[]> getYearlyData() {
        return appointmentRepository.countAppointmentByYear();
    }

    @Override
    public List<Object[]> getDailyData() {
        LocalDate lasDate = Instant.ofEpochMilli(System.currentTimeMillis()).atZone(ZoneId.of("Asia/Kolkata")).toLocalDate();
        lasDate = lasDate.minusDays(7);

        return appointmentRepository.countAppointmentsByDate(lasDate);
    }
    
}
