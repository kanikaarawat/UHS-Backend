package com.infirmary.backend.configuration.model;

import com.infirmary.backend.configuration.dto.AppointmentDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "appointment_id", nullable = false, updatable = false)
    private UUID appointmentId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sap_email", referencedColumnName = "sap_email", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_email", referencedColumnName = "doctor_email")
    private Doctor doctor;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "apt_form", referencedColumnName = "id", nullable = false)
    private AppointmentForm aptForm;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "token_no")
    private Integer tokenNo;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "prescription_id", referencedColumnName = "prescription_id")
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "location", nullable = false)
    private Location location;

    @Column(name = "weight")
    private Float weight;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "timestamp")
    private long timestamp;

    public Appointment(AppointmentDTO appointmentDTO) {
        if (appointmentDTO.getPatientDTO() != null) {
            this.patient = new Patient(appointmentDTO.getPatientDTO());
        }
        if (appointmentDTO.getDoctorDTO() != null) {
            this.doctor = new Doctor(appointmentDTO.getDoctorDTO());
        }
        if (appointmentDTO.getPrescriptionDTO() != null) {
            this.prescription = new Prescription(appointmentDTO.getPrescriptionDTO());
        }
        this.date = appointmentDTO.getDate();
        this.tokenNo = appointmentDTO.getTokenNo();
        this.temperature = appointmentDTO.getTemperature();
        this.weight = appointmentDTO.getWeight();
    }
}
