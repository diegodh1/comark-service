package com.comark.app.model.db;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("building_balance")
@Value.Immutable
@JsonSerialize(as = ImmutableBuildingBalance.class)
@JsonDeserialize(as = ImmutableBuildingBalance.class)
public interface BuildingBalance {
    @Id
    String id();
    String apartmentNumber();
    Long date();
    Double administrationCharge();
    Double monthCharge();
    Double interestRate();
    Double interestCharge();
    Double interestBalance();
    Double additionalCharge();
    Double penaltyCharge();
    Double legalCharge();
    Double otherCharge();
    Double lastBalance();
    Double totalToPaid();
    Double discount();
    Double lastPaid();
    Double finalCharge();
}
