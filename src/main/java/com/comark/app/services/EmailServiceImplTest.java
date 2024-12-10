package com.comark.app.services;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Profile("test")
public class EmailServiceImplTest implements EmailService{
    @Override
    public Mono<Void> sendEmail(String to, String subject, String body) {
        return Mono.empty();
    }

    @Override
    public Mono<Void> sendEmail(String[] recipients, String subject, String body) {
        return Mono.empty();
    }
}
