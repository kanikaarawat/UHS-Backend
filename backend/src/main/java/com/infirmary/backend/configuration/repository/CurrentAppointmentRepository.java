package com.infirmary.backend.configuration.repository;

import com.infirmary.backend.configuration.model.CurrentAppointment;
import com.infirmary.backend.configuration.model.Doctor;
import com.infirmary.backend.configuration.model.Location;

import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrentAppointmentRepository extends JpaRepository<CurrentAppointment, UUID> {

    CurrentAppointment findByAppointment_AppointmentId(@NonNull UUID appointmentId);
    Optional<CurrentAppointment> findByPatient_Email(String email);
    Optional<CurrentAppointment> findByDoctor_DoctorEmail(String doctorEmail);
    List<CurrentAppointment> findAllByAppointmentNotNullAndDoctorIsNull();
    Optional<CurrentAppointment> findByDoctor(Doctor doctor);
    int countByAppointmentNotNullAndAppointment_DateNot(LocalDate date);
    int countByAppointmentNotNull();
    List<CurrentAppointment> findAllByAppointmentNotNullAndDoctorNotNullAndAppointment_Location(Location location);
    Optional<CurrentAppointment> findTopByAppointment_DateOrderByAppointment_TokenNoDesc(LocalDate date);
    Optional<CurrentAppointment> findByDoctorIsNotNull();
        @Query("SELECT ca FROM CurrentAppointment ca WHERE ca.doctor IS NOT NULL AND ca.appointment.location.locId = :locationId")
    List<CurrentAppointment> findCurrentByLocationId(@Param("locationId") Long locationId);

    @Query("SELECT ca FROM CurrentAppointment ca WHERE ca.doctor IS NOT NULL AND ca.appointment.location.locId = :locationId")
    Optional<CurrentAppointment> findCurrentDetailsByLocationId(@Param("locationId") Long locationId);

    void deleteByPatient_Email(String email);


}