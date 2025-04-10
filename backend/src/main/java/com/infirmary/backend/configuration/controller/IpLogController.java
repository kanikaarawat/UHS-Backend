// File: IpLogController.java
package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.dto.IpLogRequestDTO;
import com.infirmary.backend.configuration.model.IpLog;
import com.infirmary.backend.configuration.repository.IpLogRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Optional: expose to frontend
public class IpLogController {

    private final IpLogRepository ipLogRepository;

    public IpLogController(IpLogRepository ipLogRepository) {
        this.ipLogRepository = ipLogRepository;
    }

    @PostMapping("/log-ip")
    public ResponseEntity<String> logUnauthorizedAccess(@RequestBody IpLogRequestDTO request) {
        IpLog log = IpLog.builder()
                .ip(request.getIp())
                .city(request.getCity())
                .region(request.getRegion())
                .country(request.getCountry())
                .org(request.getOrg())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .timestamp(LocalDateTime.now())
                .build();

        ipLogRepository.save(log);
        return ResponseEntity.ok("IP logged successfully");
    }
}
