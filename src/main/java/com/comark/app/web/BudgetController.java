package com.comark.app.web;

import com.comark.app.model.dto.budget.CompleteTaskDto;
import com.comark.app.services.BudgetService;
import lombok.NonNull;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/budget")
public class BudgetController {
    private BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity> uploadBudgetFile(@RequestPart(value = "file") FilePart excelFile) {
        return Mono.justOrEmpty(excelFile)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("invalid File")))
                .flatMap(this::getByteArray)
                .flatMap(file -> budgetService.upsertBudget(file, "actorId"))
                .map(success -> ResponseEntity.ok().build());
    }

    @GetMapping(value = "/{budgetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity> getAllTasksByBudget(@PathVariable @NonNull Integer budgetId) {
        return budgetService.getAllBudgetItemTasks(budgetId)
                .map(response -> ResponseEntity.ok().body(response));
    }

    @PostMapping("/complete")
    public Mono<ResponseEntity<Void>> completeTask(@RequestBody CompleteTaskDto completeTaskDto) {
        return budgetService.completeTask(completeTaskDto)
                .map(updatedTask -> ResponseEntity.ok().build());
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
