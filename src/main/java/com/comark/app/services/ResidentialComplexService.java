package com.comark.app.services;

import com.comark.app.model.db.ResidentialComplexItem;
import com.comark.app.model.db.ResidentialComplexItemEntity;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemEntityDto;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface ResidentialComplexService {
    Mono<Void> createResidentialComplex(String residentialId);
    Mono<Void> addResidentialComplexAdministrator(String residentialId, String email);
    Mono<List<ResidentialComplexItem>> addResidentialComplexItems(String residentialId, List<ResidentialComplexItemDto> items);
    Mono<List<ResidentialComplexItem>> getAllResidentialComplexItemsByResidentialComplexId(String residentialId, Optional<String> apartmentNumber);
    Mono<List<ResidentialComplexItem>> getAllResidentialComplexItemsByResidentialComplexIdAndEmail(String residentialId, String email);
    Mono<List<ResidentialComplexItemEntity>> addResidentialComplexItemEntities(String residentialId, List<ResidentialComplexItemEntityDto> items);
    Mono<List<ResidentialComplexItemEntity>> getAllResidentialItemEntitiesByResidentialItemId(String residentialItemId);
}
