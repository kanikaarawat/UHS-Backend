package com.infirmary.backend.configuration.securityimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class AnalyticsDetailsImpl implements UserDetailsService {
    private final DoctorDetailsImpl doctorDetailsImpl;
    private final AdDetailsImpl adDetailsImpl;
    private final AdminDetailsImpl adminDetailsImpl; // ✅ Add this

    List<UserDetailsService> usrList;

    public AnalyticsDetailsImpl(
        DoctorDetailsImpl doctorDetailsImpl,
        AdDetailsImpl adDetailsImpl,
        AdminDetailsImpl adminDetailsImpl // ✅ Inject it
    ) {
        this.doctorDetailsImpl = doctorDetailsImpl;
        this.adDetailsImpl = adDetailsImpl;
        this.adminDetailsImpl = adminDetailsImpl;
    }

    @PostConstruct
    public void setServices() {
        List<UserDetailsService> new_ser = new ArrayList<>();
        new_ser.add(adDetailsImpl);
        new_ser.add(doctorDetailsImpl);
        new_ser.add(adminDetailsImpl); // ✅ Add admin here
        this.usrList = new_ser;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        for (UserDetailsService usrServ : usrList) {
            try {
                return usrServ.loadUserByUsername(username);
            } catch (UsernameNotFoundException | ResourceNotFoundException ignored) {
                // try the next one
            }
        }
        throw new UsernameNotFoundException("No user found with username: " + username);
    }
}
