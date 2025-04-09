package com.infirmary.backend.configuration.impl;

import com.infirmary.backend.configuration.dto.UserDTO;
import com.infirmary.backend.configuration.model.Patient;
import com.infirmary.backend.configuration.repository.PatientRepository;
import com.infirmary.backend.configuration.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final PatientRepository patientRepository;

    public UserServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
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
    
        // Update relevant fields
        patient.setName(dto.getName());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setSchool(dto.getSchool());
    
        patientRepository.save(patient);
    
        return UserDTO.builder()
                .id(patient.getEmail())
                .email(patient.getEmail())
                .name(patient.getName())
                .phoneNumber(patient.getPhoneNumber())
                .bloodGroup(patient.getBloodGroup())
                .school(patient.getSchool())
                .build();
    }
    

    @Override
    public void deleteUser(String id) {
        if (!patientRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        patientRepository.deleteById(id);
    }
    

    @Override
    public Patient getUserByEmail(String email) {
        return patientRepository.findByEmail(email).orElse(null);
    }
    
}
