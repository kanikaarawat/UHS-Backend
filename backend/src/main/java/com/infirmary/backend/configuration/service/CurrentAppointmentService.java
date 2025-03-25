package com.infirmary.backend.configuration.service;

import com.infirmary.backend.configuration.Exception.CurrentAppointmentNotFoundException;
import com.infirmary.backend.configuration.Exception.DoctorNotFoundException;
import com.infirmary.backend.configuration.dto.AppointmentResDTO;
import com.infirmary.backend.configuration.dto.CurrentAppointmentDTO;

import java.util.UUID;

public interface CurrentAppointmentService {
    CurrentAppointmentDTO getCurrentAppointmentById(UUID currentAppointmentId);
    AppointmentResDTO getAppointmentStatusDoctorStatus(UUID currentAppointmentId) throws CurrentAppointmentNotFoundException;
    CurrentAppointmentDTO getCurrAppByDoctorId(String docEmail) throws CurrentAppointmentNotFoundException, DoctorNotFoundException;
    CurrentAppointmentDTO getCurrentAppointmentDetails(Long locationId);
    String getCurrentTokenNumber(Long locationId);

}