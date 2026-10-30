package com.hwsinha.carbid.bid.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PlaceBidRequest(
        @NotNull Long dealerId,
        @NotNull @Positive @Digits(integer = 10, fraction = 2)BigDecimal price,
        @Size(max = 500) String notes
        ) {
}
