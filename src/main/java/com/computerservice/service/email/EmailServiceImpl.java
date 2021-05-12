package com.computerservice.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender emailSender;

    @Override
    public void sendMessage(String email, String constructUrl, String subject) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("computerservice.heroku@gmail.com");
        message.setTo(email);
        message.setSubject(subject);
        message.setText(constructUrl);

        emailSender.send(message);
    }
}
