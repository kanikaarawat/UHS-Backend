package com.infirmary.backend.configuration.dto;

import com.infirmary.backend.configuration.model.CurrentAppointment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CurrentAppointmentDTO {
    private UUID currentAppointmentId;
    private AppointmentDTO appointmentDTO;
    private PatientDTO patientDTO;
    private DoctorDTO doctorDTO;

    public CurrentAppointmentDTO(CurrentAppointment currentAppointment) {
        this.currentAppointmentId = currentAppointment.getCurrentAppointmentId();
        
        // Null-safe initialization
        this.appointmentDTO = currentAppointment.getAppointment() != null 
            ? new AppointmentDTO(currentAppointment.getAppointment()) 
            : null;
            
        this.patientDTO = currentAppointment.getPatient() != null 
            ? new PatientDTO(currentAppointment.getPatient()) 
            : null;
            
        this.doctorDTO = currentAppointment.getDoctor() != null 
            ? new DoctorDTO(currentAppointment.getDoctor()) 
            : null;
    }
}