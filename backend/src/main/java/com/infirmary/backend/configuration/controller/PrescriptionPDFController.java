package com.infirmary.backend.configuration.controller;

import com.infirmary.backend.configuration.dto.MedicationPDFDTO;
import com.infirmary.backend.configuration.dto.PrescriptionPDFDTO;
import com.infirmary.backend.configuration.model.Appointment;
import com.infirmary.backend.configuration.model.Prescription;
import com.infirmary.backend.configuration.model.PrescriptionMeds;
import com.infirmary.backend.configuration.repository.AppointmentRepository;
import com.infirmary.backend.configuration.repository.PrescriptionMedsRepository;
import com.infirmary.backend.configuration.service.PDFService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin(origins = {
    "https://uhs.vercel.app",
    "http://localhost:5173"
}, allowCredentials = "true")
@RestController
@RequestMapping("/api/prescription")
@RequiredArgsConstructor
public class PrescriptionPDFController {

    private final AppointmentRepository appointmentRepository;
    private final PrescriptionMedsRepository prescriptionMedsRepository;
    private final PDFService pdfService;

    @GetMapping("/pdf/{appointmentId}")
    @Transactional
    public ResponseEntity<?> generatePdf(@PathVariable UUID appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));

            Prescription prescription = appointment.getPrescription();
            if (prescription == null) {
                return ResponseEntity
                        .badRequest()
                        .body("No prescription found for this appointment.");
            }

            List<PrescriptionMeds> meds = prescriptionMedsRepository.findByPrescription(prescription);

            List<MedicationPDFDTO> medsDto = meds.stream().map(med -> new MedicationPDFDTO(
                    med.getMedicine().getMedicineName(),
                    med.getDosageMorning(),
                    med.getDosageAfternoon(),
                    med.getDosageEvening(),
                    med.getDuration(),
                    med.getSuggestion()
            )).collect(Collectors.toList());

            PrescriptionPDFDTO dto = new PrescriptionPDFDTO(
                    appointment.getPatient().getName(),
                    appointment.getPatient().getSapId(),
                    appointment.getPatient().getDateOfBirth(),
                    appointment.getPatient().getSchool(),
                    appointment.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                    appointment.getCreatedAt().toLocalTime().toString(),
                    appointment.getDoctor().getDesignation(),
                    appointment.getPatient().getProgram(),
                    appointment.getPatient().getGender(),
                    medsDto,
                    prescription.getDiagnosis(),
                    prescription.getDietaryRemarks(),
                    prescription.getTestNeeded(),
                    appointment.getDoctor().getName()
            );

            // 🧠 Debugging logs
            System.out.println("Generating PDF for: " + dto.getName());
            System.out.println("Diagnosis: " + dto.getDiagnosis());
            System.out.println("Medications count: " + medsDto.size());

            byte[] pdfBytes = pdfService.generatePrescriptionPdf(dto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=prescription.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(new ByteArrayResource(pdfBytes));

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // ✅ Log full error in Render logs
            return ResponseEntity.status(500).body("PDF Generation Failed: " + e.getMessage()); // TEMPORARY
        }
    }
}
