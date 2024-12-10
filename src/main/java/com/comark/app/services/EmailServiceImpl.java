package com.comark.app.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Profile("!test")
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);


    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public Mono<Void> sendEmail(String to, String subject, String body) {
        return Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }).then().doOnError(error -> LOGGER.info("error {}", error.getMessage()));
    }

    @Override
    public Mono<Void> sendEmail(String[] recipients, String subject, String body) {
        return Mono.fromRunnable(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipients);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        }).then().doOnError(error -> LOGGER.info("error {}", error.getMessage()));
    }
}
