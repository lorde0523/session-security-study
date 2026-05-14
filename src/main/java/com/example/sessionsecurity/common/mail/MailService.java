package com.example.sessionsecurity.common.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties properties;

    public MailService(JavaMailSender javaMailSender, MailProperties properties) {
        this.javaMailSender = javaMailSender;
        this.properties = properties;
    }

    public void send(MailSendRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(properties.getFrom());
        message.setTo(request.to());
        message.setSubject(request.subject());
        message.setText(request.text());
        javaMailSender.send(message);
    }
}
