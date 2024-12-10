package com.comark.app.services;

import reactor.core.publisher.Mono;

import java.util.List;

public interface EmailService {
    Mono<Void> sendEmail(String to, String subject, String body);
    Mono<Void> sendEmail(String[] recipients, String subject, String body);
}
