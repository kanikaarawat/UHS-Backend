package com.infirmary.backend.shared.utility;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendOtpMail(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("🔐 Verify Your Login - UPES UHS");

        String htmlContent = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
          <meta charset="UTF-8" />
          <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
          <title>Verify Your Login - UPES UHS</title>
        </head>
        <body style="margin:0; padding:0; background-color:#f4f4f4; font-family:'Segoe UI', Roboto, sans-serif; color:#333;">
          <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%">
            <tr>
              <td align="center" style="padding: 40px 0;">
                <table border="0" cellpadding="0" cellspacing="0" width="600" style="background-color:#ffffff; border-radius:8px; overflow:hidden; box-shadow:0 4px 12px rgba(0,0,0,0.1);">
                  <tr>
                    <td align="center" bgcolor="#4f46e5" style="padding:40px 20px;">
                      <h1 style="margin:0; color:#ffffff;">UPES UHS Portal</h1>
                      <p style="margin:8px 0 0; color:#e0e0e0;">Your secure login access</p>
                    </td>
                  </tr>
                  <tr>
                    <td style="padding:30px;">
                      <p style="font-size:16px;">Hello,</p>
                      <p style="font-size:16px; line-height:1.5;">
                        Use the OTP below to log in securely to your <strong>UPES UHS</strong> account.
                        <br/>
                        This OTP is valid for <strong>5 minutes</strong>.
                      </p>
                      <div style="text-align:center; margin:30px 0;">
                        <span style="display: inline-block; font-size: 26px; font-weight: bold; color: #1E40AF; background-color: #E0E7FF; padding: 14px 28px; border-radius: 10px; letter-spacing: 3px;">
                          %s
                        </span>
                      </div>
                      <p style="font-size:14px; color:#888;">
                        If you did not request this OTP, you can safely ignore this email.
                      </p>
                    </td>
                  </tr>
                  <tr>
                    <td bgcolor="#f4f4f4" style="padding:20px; text-align:center; font-size:12px; color:#999;">
                      &copy; 2025 UPES UHS. All rights reserved.<br/>
                      UPES University, Bidholi, Dehradun, Uttarakhand 248007
                    </td>
                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </body>
        </html>
        """.formatted(otp);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
