package com.comark.app.web;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexAdministratorDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemEntityDto;
import com.comark.app.services.ResidentialComplexService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/residential-complex")
public class ResidentialComplexController {
    private final ResidentialComplexService residentialComplexService;

    public ResidentialComplexController(ResidentialComplexService residentialComplexService) {
        this.residentialComplexService = residentialComplexService;
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> create(@RequestBody ResidentialComplexDto request) {
        return residentialComplexService.createResidentialComplex(request.id())
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(ComarkAppException.class, error -> Mono.just(ResponseEntity.status(error.getStatusCode()).body(error.getErrorMessage())));
        
    }

    @PostMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> createResidentialComplexItems(@PathVariable String id, @RequestBody List<ResidentialComplexItemDto> items) {
        return residentialComplexService.addResidentialComplexItems(id, items)
                .then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }

    @PostMapping(value = "/admin/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> createResidentialComplexAdministrator(@PathVariable String id, @RequestBody ResidentialComplexAdministratorDto administrator) {
        return residentialComplexService.addResidentialComplexAdministrator(id, administrator.email())
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(ComarkAppException.class, error ->
                        Mono.just(ResponseEntity.status(error.getStatusCode()).body(error.getErrorMessage()))
                );
    }

    @PostMapping(value = "/{id}/{residentialComplexItemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> addResidentialComplexItemEntities(@PathVariable String id, @PathVariable String residentialComplexItemId, @RequestBody List<ResidentialComplexItemEntityDto> items) {
        return residentialComplexService.addResidentialComplexItemEntities(residentialComplexItemId, items)
                .then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }
}
