package com.comark.app.repository;

import com.comark.app.model.db.BuildingBalance;
import com.comark.app.model.db.Pqr;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PqrRepository extends ReactiveCrudRepository<Pqr, String> {
}
