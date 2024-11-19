package com.comark.app.services;

import com.comark.app.model.db.ResidentialComplexItem;
import com.comark.app.model.db.ResidentialComplexItemEntity;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemDto;
import com.comark.app.model.dto.residentialComplex.ResidentialComplexItemEntityDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ResidentialComplexService {
    Mono<Void> createResidentialComplex(String residentialId);
    Mono<Void> addResidentialComplexAdministrator(String residentialId, String email);
    Mono<List<ResidentialComplexItem>> addResidentialComplexItems(String residentialId, List<ResidentialComplexItemDto> items);
    Mono<List<ResidentialComplexItemEntity>> addResidentialComplexItemEntities(String residentialId, List<ResidentialComplexItemEntityDto> items);
}
