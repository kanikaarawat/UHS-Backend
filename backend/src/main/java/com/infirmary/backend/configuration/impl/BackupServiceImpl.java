package com.infirmary.backend.configuration.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.*;

@Service
public class BackupServiceImpl {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    private final String BACKUP_DIR = "backup/";

    private String extractDbName() {
        try {
            String[] parts = dbUrl.split("/");
            return parts[parts.length - 1].split("\\?")[0]; // strip query params
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to extract DB name from URL: " + dbUrl, e);
        }
    }

    private String extractHost() {
        try {
            String[] parts = dbUrl.split("//")[1].split("/")[0].split(":");
            return parts[0];
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to extract host from URL: " + dbUrl, e);
        }
    }

    private String extractPort() {
        try {
            String[] parts = dbUrl.split("//")[1].split("/")[0].split(":");
            return parts.length > 1 ? parts[1] : "5432";
        } catch (Exception e) {
            return "5432"; // default port
        }
    }

    public File createSystemBackup() throws IOException, InterruptedException {
        Files.createDirectories(Paths.get(BACKUP_DIR));
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String dbName = extractDbName();
        String sqlFile = BACKUP_DIR + "backup_" + timestamp + ".sql";

        System.out.println("🔧 Starting pg_dump");
        System.out.println("📦 DB: " + dbName);
        System.out.println("🔐 User: " + dbUser);
        System.out.println("🌐 Host: " + extractHost() + ", Port: " + extractPort());

        ProcessBuilder pb = new ProcessBuilder(
            "pg_dump", "-U", dbUser,
            "-h", extractHost(), "-p", extractPort(),
            "-F", "p", "-f", sqlFile, dbName
        );

        pb.environment().put("PGPASSWORD", dbPassword);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // Read output from pg_dump
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("📥 pg_dump >> " + line);
            }
        }

        int exitCode = process.waitFor();
        System.out.println("✅ pg_dump exit code: " + exitCode);

        if (exitCode != 0) {
            throw new RuntimeException("❌ pg_dump failed. See logs above.");
        }

        // Zip the .sql file
        String zipFile = BACKUP_DIR + "backup_" + timestamp + ".zip";
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
             FileInputStream fis = new FileInputStream(sqlFile)) {

            zos.putNextEntry(new ZipEntry(new File(sqlFile).getName()));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, len);
            }
        }

        Files.deleteIfExists(Paths.get(sqlFile));
        return new File(zipFile);
    }

    public void restoreSystemFromBackup(MultipartFile file) throws IOException, InterruptedException {
        Path tempDir = Files.createTempDirectory("restore");
        File zipFile = tempDir.resolve(file.getOriginalFilename()).toFile();
        file.transferTo(zipFile);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
            ZipEntry entry = zis.getNextEntry();
            while (entry != null) {
                File extracted = new File(tempDir.toFile(), entry.getName());
                try (FileOutputStream fos = new FileOutputStream(extracted)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

                System.out.println("🔁 Restoring from: " + extracted.getAbsolutePath());

                ProcessBuilder pb = new ProcessBuilder(
                    "psql", "-U", dbUser,
                    "-h", extractHost(), "-p", extractPort(),
                    "-d", extractDbName(), "-f", extracted.getAbsolutePath()
                );
                pb.environment().put("PGPASSWORD", dbPassword);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println("📥 psql >> " + line);
                    }
                }

                int exitCode = process.waitFor();
                System.out.println("✅ psql exit code: " + exitCode);

                if (exitCode != 0) {
                    throw new RuntimeException("❌ psql restore failed");
                }

                entry = zis.getNextEntry();
            }
        }
    }
}
