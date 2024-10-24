package com.comark.app.web;

import com.comark.app.model.dto.pqr.PqrDto;
import com.comark.app.services.BalanceService;
import com.comark.app.services.PqrService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/pqr")
public class PqrController {
    private final PqrService pqrService;

    public PqrController(PqrService pqrService) {
        this.pqrService = pqrService;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> uploadBudgetFile(@RequestBody PqrDto pqr) {
        return pqrService.savePqr(pqr)
                .then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }

}

