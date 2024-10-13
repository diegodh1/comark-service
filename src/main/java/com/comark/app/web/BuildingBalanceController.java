package com.comark.app.web;

import com.comark.app.services.BalanceService;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/balance")
public class BuildingBalanceController {
    private BalanceService balanceService;
    public BuildingBalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity> uploadBudgetFile(@RequestPart(value = "file") FilePart excelFile) {
        return Mono.justOrEmpty(excelFile)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("invalid File")))
                .flatMap(this::getByteArray)
                .flatMap(file -> balanceService.upsertBalance(file, "actorId"))
                .map(success -> ResponseEntity.ok().build());
    }

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAll() {
        return balanceService.getAllBalanceReports()
                .map(response -> ResponseEntity.ok().body(response));
    }

    @GetMapping(value = "/{apartmentNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAll(@PathVariable String apartmentNumber) {
        return balanceService.getBalanceReportsByApartmentNumber(apartmentNumber)
                .map(response -> ResponseEntity.ok().body(response));
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
