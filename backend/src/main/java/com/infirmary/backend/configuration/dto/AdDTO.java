package com.infirmary.backend.configuration.dto;

import com.infirmary.backend.configuration.model.AD;
import com.infirmary.backend.configuration.model.Location;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdDTO {
    @NotBlank(message = "email must not be empty")
    private String email;
    @NotBlank(message = "Password must not be empty")
    private String password;
    @NotBlank(message = "Name must not be empty")
    private String name;
    @NotBlank(message = "Designation must not be empty")
    private String designation;
    private Location location;  

    public AdDTO(AD ad){
        this.email = ad.getAdEmail();
        this.name = ad.getName();
        this.password = ad.getPassword();
        this.designation = ad.getDesignation();
        this.location = ad.getLocation();  
    }
}
