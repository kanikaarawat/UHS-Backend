package com.infirmary.backend.configuration.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infirmary.backend.configuration.Exception.UserAlreadyExists;
import com.infirmary.backend.configuration.dto.ActivityLogDTO;
import com.infirmary.backend.configuration.dto.AdDTO;
import com.infirmary.backend.configuration.dto.DoctorDTO;
import com.infirmary.backend.configuration.dto.SystemStatsDTO;
import com.infirmary.backend.configuration.service.AdminService;
import com.infirmary.backend.configuration.service.AuthService;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AuthService authService;
    private final AdminService adminService;


    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/doctor/signup")
    public ResponseEntity<?> registerDoc(@RequestBody DoctorDTO doctorDTO) throws UserAlreadyExists, UnsupportedEncodingException, MessagingException{
        return authService.signUpDat(doctorDTO);
    }

    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/AD/signup")
    public ResponseEntity<?> registerAD(@RequestBody AdDTO adDTO) throws UserAlreadyExists, UnsupportedEncodingException, MessagingException{
        return authService.signUpAD(adDTO);
    }

    // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stats")
    public ResponseEntity<SystemStatsDTO> getSystemStats() {
        SystemStatsDTO stats = adminService.getSystemStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/totalPatients")
    public ResponseEntity<Long> getTotalPatients() {
        return ResponseEntity.ok(adminService.getTotalPatients());
    }

    @GetMapping("/activeSessions")
    public ResponseEntity<Long> getActiveSessions() {
        return ResponseEntity.ok(adminService.getActiveSessions());
    }

    @GetMapping("/recentActivities")
    public ResponseEntity<List<ActivityLogDTO>> getRecentActivities() {
        return ResponseEntity.ok(adminService.getRecentActivities());
    }

    @GetMapping("/systemHealth")
    public ResponseEntity<Map<String, Object>> getSystemHealth() {
        return ResponseEntity.ok(adminService.getSystemHealth());
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<String>> getAlerts() {
        return ResponseEntity.ok(adminService.getCurrentAlerts());
    }


}
