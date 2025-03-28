package com.infirmary.backend.configuration.dto;

import com.infirmary.backend.configuration.model.AppointmentForm;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class AppointmentReqDTO {

    @NotBlank(message = "Please provide the patient's email")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Please provide reason for visit")
    private String reason;

    private Boolean isFollowUp;
    private UUID preferredDoctor;
    private String reasonPrefDoctor;

    public AppointmentReqDTO(AppointmentForm appointmentForm) {
        this.reason = appointmentForm.getReason();
        this.isFollowUp = appointmentForm.getIsFollowUp();
        this.reasonPrefDoctor = appointmentForm.getReasonForPreference();
    }
}
