package com.infirmary.backend.shared.utility;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendOtpMail(String to, String otp) throws MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP for Login - UHS Portal");
        message.setText("Your OTP is: " + otp + "\n\nIt will expire in 5 minutes.\n\n- UHS System");

        mailSender.send(message);
    }
}
