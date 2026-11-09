package com.hwsinha.carbid.bid.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PlaceBidRequest(
        @Schema(example = "1299900") @NotNull @Positive @Digits(integer = 10, fraction = 2)BigDecimal price,
        @Schema(example = "Free floor mats, 1 year insurance") @Size(max = 500) String notes
        ) {
}
