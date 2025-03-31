package com.infirmary.backend.configuration.service;

import com.infirmary.backend.configuration.dto.UserDTO;
import com.infirmary.backend.configuration.model.Patient;
import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO createUser(UserDTO dto);
    UserDTO updateUser(String id, UserDTO dto);
    void deleteUser(String id);
    Patient getUserByEmail(String email);
}
