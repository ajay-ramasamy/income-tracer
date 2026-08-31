package com.dev.money.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;
    @Value("${spring.mail.username}")
    private String smtpUsername;
    @Value("${spring.mail.password}")
    private String smtpPassword;

    public void sendEmail(String to,String subject, String Body){
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(Body);

            mailSender.send(message);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Email sending failed", e);
        }
    }

}
