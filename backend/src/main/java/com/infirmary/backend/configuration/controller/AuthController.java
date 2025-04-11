package com.infirmary.backend.configuration.controller;


import static com.infirmary.backend.shared.utility.FunctionUtil.createSuccessResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infirmary.backend.configuration.Exception.UserAlreadyExists;
import com.infirmary.backend.configuration.dto.LoginRequestDTO;
import com.infirmary.backend.configuration.dto.PasswordChangeDTO;
import com.infirmary.backend.configuration.dto.PatientReqDTO;
import com.infirmary.backend.configuration.service.AuthService;
import com.infirmary.backend.configuration.dto.OtpLoginRequestDTO;
import com.infirmary.backend.configuration.dto.OtpVerificationRequestDTO;
import com.infirmary.backend.configuration.service.OtpService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    private AuthService authService;
    private final OtpService otpService;

    public AuthController(AuthService authService, OtpService otpService){
        this.authService = authService;
        this.otpService = otpService;
    }
    

    @PostMapping("patient/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest,BindingResult bindingResult){
        return authService.loginServicePat(loginRequest);
    }

    @PostMapping("ad/signin")
    public ResponseEntity<?> authenticateAd(@Valid @RequestBody LoginRequestDTO loginRequestDTO, BindingResult bindingResult,@RequestHeader(name = "X-Latitude",required = true) Double latitude,@RequestHeader(name = "X-Longitude", required = true) Double longitude){
        return authService.loginServiceAd(loginRequestDTO,latitude,longitude);
    }

    @PostMapping("doctor/signin")
    public ResponseEntity<?> authenticateDoc(@Valid @RequestBody LoginRequestDTO loginRequestDTO,BindingResult bindingResult){
        return authService.loginServiceDat(loginRequestDTO);
    }

    @PostMapping("admin/signin")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginRequestDTO loginRequest, BindingResult bindingResult){
        return authService.loginServiceAdmin(loginRequest);
    }

    @PostMapping("/patient/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody PatientReqDTO patientDTO, BindingResult bindingResult) throws UserAlreadyExists, IOException, MessagingException{
        return authService.signUpPat(patientDTO);
    }

    @GetMapping("/user/verify")
    public ResponseEntity<?> verifyPatient(@RequestParam(name = "code") UUID code){
        return createSuccessResponse(authService.verifyUser(code));
    }

    @GetMapping("/passwordChangeRequest")
    public ResponseEntity<?> passwordChangeReq(@RequestParam(name = "email") String email, @RequestParam(name = "role") String role) throws UnsupportedEncodingException, MessagingException{
        return createSuccessResponse(role.equals("patient")?authService.forgetPassPat(email):role.equals("doctor")?authService.forgetPassDoc(email):authService.forgetPassAd(email));
    }

    @PostMapping("/passwordChange")
    public ResponseEntity<?> passwordChange(@RequestParam(name = "code") UUID code, @RequestParam(name = "role") String role, @Valid @RequestBody PasswordChangeDTO passwordChange,BindingResult bindingResult) throws UnsupportedEncodingException, MessagingException{
        return createSuccessResponse(role.equals("patient")?authService.changePassPat(code, passwordChange):role.equals("doctor")?authService.changePassDoc(code, passwordChange):authService.changePassPat(code, passwordChange));
    }

    @PostMapping("/test")
    public List<?> test(){
        throw new IllegalAccessError();
    }

    @PostMapping("/otp/send")
    public ResponseEntity<?> sendOtp(@RequestBody OtpLoginRequestDTO request) {
        try {
            otpService.generateAndSendOtp(request.getEmail(), request.getPhoneNumber()); // ✅ Method name fixed
            return ResponseEntity.ok().body("OTP sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to send OTP: " + e.getMessage());
        }
    }
    
    @PostMapping("/otp/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerificationRequestDTO request) {
        try {
            String jwtToken = otpService.verifyOtp(request.getEmail(), request.getOtp()); // ✅ 2 args only
            return ResponseEntity.ok().body(Map.of("token", jwtToken));
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid OTP: " + e.getMessage());
        }
    }
    
}