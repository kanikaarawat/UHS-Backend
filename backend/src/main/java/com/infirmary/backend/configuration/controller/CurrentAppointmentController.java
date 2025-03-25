package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.dto.CurrentAppointmentDTO;
import com.infirmary.backend.configuration.service.CurrentAppointmentService;
import com.infirmary.backend.shared.utility.FunctionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/current-appointment")
public class CurrentAppointmentController {
    private final CurrentAppointmentService currentAppointmentService;

    @Autowired
    public CurrentAppointmentController(CurrentAppointmentService currentAppointmentService) {
        this.currentAppointmentService = currentAppointmentService;
    }

  @GetMapping("/current-token")
  public ResponseEntity<String> getCurrentTokenNumber(@RequestParam Long locationId) {
    String token = currentAppointmentService.getCurrentTokenNumber(locationId);
    return ResponseEntity.ok(token);
  }

    @GetMapping("/details")
    @PreAuthorize("hasRole('ROLE_AD') or hasRole('ROLE_PATIENT')")
    public ResponseEntity<?> getCurrentAppointmentDetails(
        @RequestParam Long locationId
    ) {
        CurrentAppointmentDTO currentAppointment = currentAppointmentService.getCurrentAppointmentDetails(locationId);
        return FunctionUtil.createSuccessResponse(currentAppointment);
    }
}