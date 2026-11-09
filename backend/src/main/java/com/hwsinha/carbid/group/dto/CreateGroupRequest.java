package com.hwsinha.carbid.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateGroupRequest(
        @Schema(example = "1") @NotNull Long carId,
        @Schema(example = "Mumbai") @NotBlank String city,
        @Schema(example = "3") @Min(2) int minMembers,
        @Schema(example = "2026-12-31T10:00:00Z") @NotNull @Future Instant deadline
        ) {
}
