package com.fightnet.security.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailService {
  
    private final JavaMailSender emailSender;
 
    public void sendCodeMessage(final String to, final String subject, final String text) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to); 
        message.setSubject(subject); 
        message.setText(text);
        emailSender.send(message);
    }
}