package com.infirmary.backend.configuration.impl;
import com.infirmary.backend.configuration.dto.UserDTO;
import com.infirmary.backend.configuration.model.Appointment;
import com.infirmary.backend.configuration.model.Patient;
import com.infirmary.backend.configuration.repository.AdPrescriptionRepository;
import com.infirmary.backend.configuration.repository.AppointmentRepository;
import com.infirmary.backend.configuration.repository.ConformationRepository;
import com.infirmary.backend.configuration.repository.CurrentAppointmentRepository;
import com.infirmary.backend.configuration.repository.MedicalDetailsRepository;
import com.infirmary.backend.configuration.repository.PasswordChangeRepository;
import com.infirmary.backend.configuration.repository.PatientRepository;
import com.infirmary.backend.configuration.repository.PrescriptionRepository;
import com.infirmary.backend.configuration.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserServiceImpl implements UserService {
    private final MedicalDetailsRepository medicalDetailsRepository;
    private final AdPrescriptionRepository adPrescriptionRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ConformationRepository conformationRepository;
    private final PatientRepository patientRepository;
    private final CurrentAppointmentRepository currentAppointmentRepository;
    private PasswordChangeRepository passwordChangeRepository;
    private final PasswordEncoder passwordEncoder; // already used for hashing elsewhere


    public UserServiceImpl(
        PatientRepository patientRepository,
        MedicalDetailsRepository medicalDetailsRepository,
        AdPrescriptionRepository adPrescriptionRepository,
        AppointmentRepository appointmentRepository,
        PrescriptionRepository prescriptionRepository,
        CurrentAppointmentRepository currentAppointmentRepository,
        ConformationRepository conformationRepository,
        PasswordChangeRepository passwordChangeRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.medicalDetailsRepository = medicalDetailsRepository;
        this.adPrescriptionRepository = adPrescriptionRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.currentAppointmentRepository = currentAppointmentRepository;
        this.conformationRepository = conformationRepository;
        this.patientRepository = patientRepository;
        this.passwordChangeRepository = passwordChangeRepository;
        this.passwordEncoder = passwordEncoder;
    }    

    @Override
    public List<UserDTO> getAllUsers() {
        return patientRepository.findAll().stream()
            .map(p -> UserDTO.builder()
                .id(p.getEmail())                  // or p.getId() if you have a UUID/PK
                .sapId(p.getSapId())               // ✅ Set SAP ID here
                .email(p.getEmail())
                .name(p.getName())
                .phoneNumber(p.getPhoneNumber())
                .bloodGroup(p.getBloodGroup())
                .school(p.getSchool())
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO dto) {
        throw new UnsupportedOperationException("Cannot create user directly in patient table");
    }

    @Override
    public UserDTO updateUser(String id, UserDTO dto) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found: " + id));
    
        // ✅ Update all editable fields
        patient.setName(dto.getName());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setSchool(dto.getSchool());
        patient.setSapId(dto.getSapId());
        patient.setEmergencyContact(dto.getEmergencyContact());
        patient.setGender(dto.getGender());
        patient.setProgram(dto.getProgram());
        if (dto.getDateOfBirth() != null) {
            patient.setDateOfBirth(dto.getDateOfBirth());
        }
    
        patientRepository.save(patient);
    
        return UserDTO.builder()
                .id(patient.getEmail())
                .email(patient.getEmail())
                .name(patient.getName())
                .phoneNumber(patient.getPhoneNumber())
                .bloodGroup(patient.getBloodGroup())
                .school(patient.getSchool())
                .sapId(patient.getSapId())
                .emergencyContact(patient.getEmergencyContact())
                .gender(patient.getGender())
                .program(patient.getProgram())
                .build();
    }
    
@Override
@Transactional
public void deleteUser(String email) {
    @SuppressWarnings("unused")
    Patient patient = patientRepository.findById(email)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

    System.out.println("Deleting user: " + email);
    List<Appointment> appointments = appointmentRepository.findByPatient_Email(email);

    for (@SuppressWarnings("unused") Appointment appointment : appointments) {
        currentAppointmentRepository.deleteByPatient_Email(email);
    }
    passwordChangeRepository.deleteByPatientEmail(email);
    conformationRepository.deleteByPatient_Email(email);
    prescriptionRepository.deleteByPatientEmail(email);
    adPrescriptionRepository.deleteByPatientEmail(email);
    appointmentRepository.deleteByPatientEmail(email);
    medicalDetailsRepository.deleteByPatientEmail(email);

    // Step 5: Finally, delete patient
    patientRepository.deleteById(email);
}



@Override
public Patient getUserByEmail(String email) {
    return patientRepository.findByEmail(email).orElse(null);
}
@Override
public void updatePassword(String email, String newPassword) {
    Patient patient = patientRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("User not found"));

    patient.setPassword(passwordEncoder.encode(newPassword));
    patientRepository.save(patient);
}

    
}
