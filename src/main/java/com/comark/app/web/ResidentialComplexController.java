package com.comark.app.web;

import com.comark.app.exception.ComarkAppException;
import com.comark.app.model.dto.error.ImmutableComarkAppErrorDto;
import com.comark.app.model.dto.residentialComplex.*;
import com.comark.app.services.ResidentialComplexService;
import lombok.NonNull;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
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
                .onErrorResume(ComarkAppException.class, error ->
                        Mono.just(ResponseEntity.status(error.getStatusCode()).body(ImmutableComarkAppErrorDto.builder().code(error.getStatusCode()).message(error.getErrorMessage()).build()))
                );
        
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
                        Mono.just(ResponseEntity.status(error.getStatusCode()).body(ImmutableComarkAppErrorDto.builder().code(error.getStatusCode()).message(error.getErrorMessage()).build()))
                );
    }

    @PostMapping(value = "/event/{residentialComplexId}/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> createResidentialComplexItemEvent(@PathVariable String residentialComplexId, @PathVariable String id, @RequestBody ResidentialComplexEventDto event) {
        return residentialComplexService.createResidentialComplexItemEvent(residentialComplexId, id, event.name(), event.description(), event.restriction(), event.startDateTime(), event.endDateTime(), event.organizerId())
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(ComarkAppException.class, error ->
                        Mono.just(ResponseEntity.status(error.getStatusCode()).body(ImmutableComarkAppErrorDto.builder().code(error.getStatusCode()).message(error.getErrorMessage()).build()))
                );
    }

    @PostMapping(value = "/event/{eventId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Object>> updateResidentialComplexItemEvent(@PathVariable String eventId, @RequestBody ResidentialComplexEventUpdateRequestDto event) {
        return residentialComplexService.updateResidentialComplexItemEventStatus(eventId, event.status())
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(ComarkAppException.class, error ->
                        Mono.just(ResponseEntity.status(error.getStatusCode()).body(ImmutableComarkAppErrorDto.builder().code(error.getStatusCode()).message(error.getErrorMessage()).build()))
                );
    }

    @GetMapping(value = "/events/{residentialComplexId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAllEvents(@PathVariable String residentialComplexId) {
        return residentialComplexService.getAllPendingEvents(residentialComplexId)
                .flatMap(items -> Mono.just(ResponseEntity.ok().body(items)));
    }

    @GetMapping(value = "/zones/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAllZonasComunesByResidentialComplexItemId(@PathVariable String id) {
        return residentialComplexService.getAllResidentialComplexItemsTypeEqualsToZonaComun(id)
                .flatMap(items -> Mono.just(ResponseEntity.ok().body(items)));
    }

    @PostMapping(value = "/{id}/{residentialComplexItemId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> addResidentialComplexItemEntities(@PathVariable String id, @PathVariable String residentialComplexItemId, @RequestBody List<ResidentialComplexItemEntityDto> items) {
        return residentialComplexService.addResidentialComplexItemEntities(residentialComplexItemId, items)
                .then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }

    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Object>> loadAndUpsertResidentialComplexInformation(@PathVariable String id, @RequestPart(value = "file") FilePart excelFile) {
        return Mono.justOrEmpty(excelFile)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("invalid File")))
                .flatMap(this::getByteArray)
                .flatMap(data -> residentialComplexService.loadAndUpsertResidentialComplexInformation(data, id))
                .then(Mono.just(ResponseEntity.ok().build()))
                .onErrorResume(ComarkAppException.class, error ->
                        Mono.just(ResponseEntity.status(error.getStatusCode()).body(ImmutableComarkAppErrorDto.builder().code(error.getStatusCode()).message(error.getErrorMessage()).build()))
                );
    }

    private Mono<byte[]> getByteArray(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .flatMap(dataBuffer -> {
                    // Create a new byte array and copy data from DataBuffer
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    // Release the DataBuffer after usage
                    DataBufferUtils.release(dataBuffer);
                    return Mono.just(bytes);
                });
    }
}
