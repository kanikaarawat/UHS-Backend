package com.infirmary.backend.configuration.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.infirmary.backend.configuration.Exception.StockAlreadyExists;
import com.infirmary.backend.configuration.Exception.UserAlreadyExists;
import com.infirmary.backend.configuration.dto.*;
import com.infirmary.backend.configuration.service.AdminService;
import com.infirmary.backend.configuration.service.AuthService;
import com.infirmary.backend.configuration.service.StockService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.infirmary.backend.shared.utility.FunctionUtil.createSuccessResponse;

@RestController
@RequestMapping(value = "/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;
    private final StockService stockService;

    // Doctor CRUD
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/doctor/signup")
    public ResponseEntity<?> registerDoc(@RequestBody DoctorDTO doctorDTO)
            throws UserAlreadyExists, UnsupportedEncodingException, MessagingException {
        return authService.signUpDat(doctorDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/doctor")
    public ResponseEntity<List<DoctorDTO>> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/doctor/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable UUID id) {
        return ResponseEntity.ok(adminService.getDoctorById(id));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/doctor/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable UUID id, @RequestBody DoctorDTO dto) {
        return ResponseEntity.ok(adminService.updateDoctor(id, dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/doctor/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable UUID id) {
        adminService.deleteDoctor(id);
        return ResponseEntity.ok("Doctor deleted successfully");
    }

    // AD (Nursing Assistant) CRUD
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/AD/signup")
    public ResponseEntity<?> registerAD(@RequestBody AdDTO adDTO)
            throws UserAlreadyExists, UnsupportedEncodingException, MessagingException {
        return authService.signUpAD(adDTO);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/ad")
    public ResponseEntity<List<AdDTO>> getAllADs() {
        return ResponseEntity.ok(adminService.getAllAssistants());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/ad/{email}")
    public ResponseEntity<AdDTO> getAdByEmail(@PathVariable String email) {
        return ResponseEntity.ok(adminService.getAssistantById(email));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/ad/{email}")
    public ResponseEntity<AdDTO> updateAd(@PathVariable String email, @RequestBody AdDTO dto) {
        return ResponseEntity.ok(adminService.updateAssistant(email, dto));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/ad/{email}")
    public ResponseEntity<?> deleteAd(@PathVariable String email) {
        adminService.deleteAssistant(email);
        return ResponseEntity.ok("Assistant deleted successfully");
    }

    // System stats
    @GetMapping("/stats")
    public ResponseEntity<SystemStatsDTO> getSystemStats() {
        return ResponseEntity.ok(adminService.getSystemStats());
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

    // Stock management
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stock/")
    public ResponseEntity<?> getAllStock() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stock/available")
    public ResponseEntity<?> getAvailableStock(
            @RequestHeader(name = "X-Latitude") Double latitude,
            @RequestHeader(name = "X-Longitude") Double longitude) {
        return ResponseEntity.ok(stockService.getAvailableStock(longitude, latitude));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stock/addStock")
    public ResponseEntity<?> addStock(@Valid @RequestBody StockDTO dto,
                                      BindingResult bindingResult,
                                      @RequestHeader(value = "X-Location") Long locId) throws StockAlreadyExists {
        return createSuccessResponse(stockService.addStock(dto, locId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/stock/editStock")
    public ResponseEntity<?> editStock(@Valid @RequestBody StockDTO dto,
                                       BindingResult bindingResult,
                                       @RequestHeader(value = "X-Location") Long locId) throws StockAlreadyExists {
        return createSuccessResponse(stockService.editStock(dto, locId));
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportStocks(@RequestParam(defaultValue = "all") String filter) throws IOException {
        byte[] file = stockService.exportStocksToExcel(filter);
        String filename = "medicine_stocks_" + filter + ".xlsx";
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(file);
    }
}
