package com.comark.app.web;

import com.comark.app.model.dto.email.EmailDto;
import com.comark.app.model.dto.pqr.PqrDto;
import com.comark.app.services.EmailService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {
    private final EmailService emailService;

    public MaintenanceController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping(value = "/sendEmail", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> send(@RequestBody EmailDto email) {
        return emailService.sendEmail(email.emailTo(), email.subject(), email.message())
                .then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }
}
