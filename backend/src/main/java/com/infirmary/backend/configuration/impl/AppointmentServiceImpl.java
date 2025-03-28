package com.infirmary.backend.configuration.impl;

import java.util.Map;
import com.infirmary.backend.configuration.Exception.AppointmentNotFoundException;
import com.infirmary.backend.configuration.Exception.DoctorNotFoundException;
import com.infirmary.backend.configuration.Exception.PatientNotFoundException;
import com.infirmary.backend.configuration.Exception.PrescriptionNotFoundException;
import com.infirmary.backend.configuration.dto.AppointmentDTO;
import com.infirmary.backend.configuration.dto.AppointmentReqDTO;
import com.infirmary.backend.configuration.dto.PrescriptionDTO;
import com.infirmary.backend.configuration.model.*;
import com.infirmary.backend.configuration.repository.*;
import com.infirmary.backend.configuration.service.AppointmentService;
import com.infirmary.backend.shared.utility.AppointmentQueueManager;
import com.infirmary.backend.shared.utility.MessageConfigUtil;
import com.infirmary.backend.configuration.repository.AdRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static com.infirmary.backend.shared.utility.FunctionUtil.createSuccessResponse;

@Slf4j
@Transactional
@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final MessageConfigUtil messageConfigUtil;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentFormRepository appointmentFormRepository;
    private final AdRepository adRepository;
    private final CurrentAppointmentRepository currentAppointmentRepository;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            MessageConfigUtil messageConfigUtil,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentFormRepository appointmentFormRepository,
            AdRepository adRepository,
            CurrentAppointmentRepository currentAppointmentRepository) {
        this.appointmentRepository = appointmentRepository;
        this.messageConfigUtil = messageConfigUtil;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentFormRepository = appointmentFormRepository;
        this.adRepository = adRepository;
        this.currentAppointmentRepository = currentAppointmentRepository;
    }

    @Override
    public AppointmentDTO getAppointmentById(UUID appointmentId) throws AppointmentNotFoundException {
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
        return new AppointmentDTO(appointment);
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByPatientId(String email) throws AppointmentNotFoundException, PatientNotFoundException {
        if (Objects.isNull(email)) {
            throw new PatientNotFoundException(messageConfigUtil.getPatientNotFound());
        }
        List<Appointment> appointmentList = appointmentRepository.findByPatient_Email(email);
        if (appointmentList.isEmpty()) {
            throw new AppointmentNotFoundException(messageConfigUtil.getAppointmentNotFoundException());
        }
        return appointmentList.stream().map(AppointmentDTO::new).toList();
    }

    @Override
    public List<AppointmentDTO> getAppointmentsByDoctorId(String doctorId) throws DoctorNotFoundException, AppointmentNotFoundException {
        if (Objects.isNull(doctorId)) {
            throw new DoctorNotFoundException(messageConfigUtil.getDoctorNotFoundException());
        }
        List<Appointment> appointmentList = appointmentRepository.findByDoctor_DoctorEmail(doctorId);
        if (appointmentList.isEmpty()) {
            throw new AppointmentNotFoundException(messageConfigUtil.getAppointmentNotFoundException());
        }
        return appointmentList.stream().map(AppointmentDTO::new).toList();
    }

    @Override
    public LocalDate getLastAppointmentDateByEmail(String patientEmail) throws AppointmentNotFoundException {
        Optional<Appointment> lastAppointment = appointmentRepository.findFirstByPatient_EmailOrderByDateDesc(patientEmail);
        if (lastAppointment.isEmpty()) {
            throw new AppointmentNotFoundException(messageConfigUtil.getAppointmentNotFoundException());
        }
        return lastAppointment.map(Appointment::getDate).orElse(null);
    }

    @Override
    public void scheduleAppointment(UUID appointmentId) {
        AppointmentQueueManager.addAppointmentToQueue(appointmentId);
    }

    @Override
    public UUID getNextAppointment() {
        if (AppointmentQueueManager.hasMoreAppointments()) {
            return AppointmentQueueManager.getNextAppointment();
        }
        throw new RuntimeException("Queue empty!");
    }

    @Override
    public AppointmentDTO getCurrentNextAppointment() throws AppointmentNotFoundException {
        UUID nextId = getNextAppointment();
        return getAppointmentById(nextId);
    }

    @Override
    public List<Prescription> getPrescriptionUrlByPatientEmail(String email) throws PatientNotFoundException {
        if (Objects.isNull(email)) {
            throw new PatientNotFoundException(messageConfigUtil.getPatientNotFound());
        }
        List<Appointment> byEmail = appointmentRepository.findByPatient_Email(email);
        List<Prescription> list = byEmail.stream().map(Appointment::getPrescription).toList();
        if (list.isEmpty()) {
            throw new RuntimeException("No prescription URLs found.");
        }
        return list;
    }

    @Override
    public PrescriptionDTO getPrescriptionByAppointmentId(UUID appointmentId) throws PatientNotFoundException, PrescriptionNotFoundException {
        if (Objects.isNull(appointmentId)) {
            throw new AppointmentNotFoundException(messageConfigUtil.getAppointmentNotFoundException());
        }
        Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
        Prescription prescription = appointment.getPrescription();
        if (Objects.isNull(prescription)) {
            throw new PrescriptionNotFoundException(messageConfigUtil.getPrescriptionNotFoundException());
        }
        return new PrescriptionDTO(prescription);
    }

    @Override
    public ResponseEntity<?> manualSubmitAppointment(AppointmentReqDTO req, String adEmail) {
        Patient patient = patientRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with email: " + req.getEmail()));

        AppointmentForm aptForm = new AppointmentForm();
        aptForm.setReason(req.getReason());
        aptForm.setIsFollowUp(false);
        aptForm.setReasonForPreference(req.getReasonPrefDoctor());

        if (req.getPreferredDoctor() != null) {
            Doctor doctor = doctorRepository.findById(req.getPreferredDoctor())
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
            aptForm.setPrefDoctor(doctor);
        }

        aptForm = appointmentFormRepository.save(aptForm);

        AD ad = adRepository.findByAdEmail(adEmail)
                .orElseThrow(() -> new ResourceNotFoundException("AD not found"));
        if (ad.getLocation() == null)
            throw new IllegalArgumentException("AD not assigned to a location");

        Integer maxToken = appointmentRepository.findMaxTokenNoForDateAndLocation(LocalDate.now(), ad.getLocation().getLocId());
        int newToken = maxToken + 1;

        Appointment appointment = new Appointment();
        appointment.setAptForm(aptForm);
        appointment.setPatient(patient);
        appointment.setDate(LocalDate.now());
        appointment.setTimestamp(System.currentTimeMillis());
        appointment.setTokenNo(newToken);
        appointment.setLocation(ad.getLocation());
        appointment.setDoctor(null);
        appointment = appointmentRepository.save(appointment);

        CurrentAppointment current = currentAppointmentRepository.findByPatient_Email(req.getEmail())
                .orElseGet(() -> {
                    CurrentAppointment newCurrent = new CurrentAppointment();
                    newCurrent.setPatient(patient);
                    return newCurrent;
                });
        current.setAppointment(appointment);
        currentAppointmentRepository.save(current);

        AppointmentQueueManager.addAppointmentToQueue(appointment.getAppointmentId());

        return createSuccessResponse(Map.of(
                "message", "Manual appointment created",
                "appointmentId", appointment.getAppointmentId(),
                "tokenNo", newToken
        ));
    }
}
