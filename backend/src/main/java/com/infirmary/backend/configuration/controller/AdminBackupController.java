package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.impl.BackupServiceImpl;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@RequestMapping("/api/admin")
public class AdminBackupController {

    private final BackupServiceImpl backupService;

    public AdminBackupController(BackupServiceImpl backupService) {
        this.backupService = backupService;
    }

    @PostMapping("/backup")
    public ResponseEntity<Resource> createBackup() {
        try {
            File backup = backupService.createSystemBackup();
            InputStreamResource resource = new InputStreamResource(new FileInputStream(backup));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + backup.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(backup.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/restore")
    public ResponseEntity<String> restoreSystem(@RequestParam("file") MultipartFile file) {
        try {
            backupService.restoreSystemFromBackup(file);
            return ResponseEntity.ok("System restored successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Restore failed: " + e.getMessage());
        }
    }
}
