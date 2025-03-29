package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.repository.AppointmentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentController(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @GetMapping("/countToday")
    public ResponseEntity<Integer> countTodayAppointments() {
        int count = appointmentRepository.countTodayAppointments();
        return ResponseEntity.ok(count);
    }
}
