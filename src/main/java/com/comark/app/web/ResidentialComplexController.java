package com.comark.app.web;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexAdministratorDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemEntityDto;
import com.comark.app.services.ResidentialComplexService;
import jakarta.ws.rs.QueryParam;
import lombok.NonNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

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

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAllResidentialComplexItemsByResidentialComplexId(@PathVariable String id, @Nullable @RequestParam Optional<String> apartmentNumber) {
        return residentialComplexService.getAllResidentialComplexItemsByResidentialComplexId(id, apartmentNumber)
                .flatMap(items -> Mono.just(ResponseEntity.ok().body(items)));
    }

    @GetMapping(value = "/item/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAllResidentialComplexItemEntitiesByItemId(@PathVariable String id) {
        return residentialComplexService.getAllResidentialItemEntitiesByResidentialItemId(id)
                .flatMap(items -> Mono.just(ResponseEntity.ok().body(items)));
    }

    @GetMapping(value = "/admin/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAllResidentialComplexItemsByResidentialComplexId(@PathVariable String id, @NonNull @RequestParam String email) {
        return residentialComplexService.getAllResidentialComplexItemsByResidentialComplexIdAndEmail(id, email)
                .flatMap(items -> Mono.just(ResponseEntity.ok().body(items)));
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
