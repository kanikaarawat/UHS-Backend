package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.dto.CurrentAppointmentDTO;
import com.infirmary.backend.configuration.service.CurrentAppointmentService;
import com.infirmary.backend.shared.utility.FunctionUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/current-appointment")
public class CurrentAppointmentController {

    private final CurrentAppointmentService currentAppointmentService;

    @Autowired
    public CurrentAppointmentController(CurrentAppointmentService currentAppointmentService) {
        this.currentAppointmentService = currentAppointmentService;
    }

    /**
     * Fetch the current token number being treated by the doctor.
     *
     * @return ResponseEntity with the current token number.
     */
    @PreAuthorize("hasRole('ROLE_AD') or hasRole('ROLE_PATIENT')")
    @GetMapping("/current-token")
    public ResponseEntity<?> getCurrentTokenNumber() {

        String currentTokenNumber = currentAppointmentService.getCurrentTokenNumber();
        return ResponseEntity.ok(currentTokenNumber);
    }

    /**
     * Fetch the current appointment details.
     *
     * @return ResponseEntity with the current appointment details.
     */
    @PreAuthorize("hasRole('ROLE_AD') or hasRole('ROLE_PATIENT')")
    @GetMapping("/details")
    public ResponseEntity<?> getCurrentAppointmentDetails() {
        CurrentAppointmentDTO currentAppointment = currentAppointmentService.getCurrentAppointmentDetails();
        return FunctionUtil.createSuccessResponse(currentAppointment);
    }
}