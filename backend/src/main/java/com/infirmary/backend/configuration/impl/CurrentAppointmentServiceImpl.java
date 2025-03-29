package com.infirmary.backend.configuration.impl;

import com.infirmary.backend.configuration.Exception.CurrentAppointmentNotFoundException;
import com.infirmary.backend.configuration.Exception.DoctorNotFoundException;
import com.infirmary.backend.configuration.dto.AppointmentResDTO;
import com.infirmary.backend.configuration.dto.CurrentAppointmentDTO;
import com.infirmary.backend.configuration.model.CurrentAppointment;
import com.infirmary.backend.configuration.repository.CurrentAppointmentRepository;
import com.infirmary.backend.configuration.service.CurrentAppointmentService;
import com.infirmary.backend.shared.utility.MessageConfigUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.List;


@Service
@Slf4j
@Transactional
public class CurrentAppointmentServiceImpl implements CurrentAppointmentService {
    private final CurrentAppointmentRepository currentAppointmentRepository;
    private final MessageConfigUtil messageConfigUtil;

    public CurrentAppointmentServiceImpl(CurrentAppointmentRepository currentAppointmentRepository, 
                                       MessageConfigUtil messageConfigUtil) {
        this.currentAppointmentRepository = currentAppointmentRepository;
        this.messageConfigUtil = messageConfigUtil;
    }

    @Override
    public CurrentAppointmentDTO getCurrentAppointmentById(UUID currentAppointmentId) {
        CurrentAppointment appointment = currentAppointmentRepository.findByAppointment_AppointmentId(currentAppointmentId);
        if (Objects.isNull(appointment)) {
            throw new CurrentAppointmentNotFoundException(messageConfigUtil.getCurrentAppointmentNotFound());
        }
        return new CurrentAppointmentDTO(appointment);
    }

    @Override
    public AppointmentResDTO getAppointmentStatusDoctorStatus(UUID currentAppointmentId) throws CurrentAppointmentNotFoundException {
        AppointmentResDTO appointmentResDTO = new AppointmentResDTO();
        appointmentResDTO.setIsAppointedStatus(null);
        appointmentResDTO.setIsDoctorAppointed(null);

        CurrentAppointmentDTO currentAppointmentDTO = getCurrentAppointmentById(currentAppointmentId);
        if (Objects.isNull(currentAppointmentDTO)) {
            throw new CurrentAppointmentNotFoundException(messageConfigUtil.getCurrentAppointmentNotFound());
        }

        if (currentAppointmentDTO.getAppointmentDTO() == null) {
            appointmentResDTO.setIsAppointedStatus(false);
        } else {
            appointmentResDTO.setIsAppointedStatus(true);
            appointmentResDTO.setIsDoctorAppointed(currentAppointmentDTO.getDoctorDTO() != null);
        }
        
        return appointmentResDTO;
    }

    @Override
    public CurrentAppointmentDTO getCurrAppByDoctorId(String docEmail) throws CurrentAppointmentNotFoundException, DoctorNotFoundException {
        if (Objects.isNull(docEmail)) {
            throw new DoctorNotFoundException(messageConfigUtil.getDoctorNotFoundException());
        }
        CurrentAppointment currAppointment = currentAppointmentRepository.findByDoctor_DoctorEmail(docEmail)
                .orElseThrow(() -> new ResourceNotFoundException("No Appointment Scheduled"));
        return new CurrentAppointmentDTO(currAppointment);
    }

    @Override
    public String getCurrentTokenNumber(Long locationId) {
        List<CurrentAppointment> currentAppointments =
            currentAppointmentRepository.findCurrentByLocationId(locationId);
    
        if (currentAppointments == null || currentAppointments.isEmpty()) {
            log.warn("No current appointment found for location {}", locationId);
            return "N/A";
        }
    
        return currentAppointments.stream()
            .map(CurrentAppointment::getAppointment)
            .filter(Objects::nonNull)
            .map(app -> app.getTokenNo())
            .filter(Objects::nonNull)
            .max(Integer::compareTo)
            .map(String::valueOf)
            .orElse("N/A");
    }

    @Override
    public CurrentAppointmentDTO getCurrentAppointmentDetails(Long locationId) {
        throw new UnsupportedOperationException("Unimplemented method 'getCurrentAppointmentDetails'");
    }
    
    

}