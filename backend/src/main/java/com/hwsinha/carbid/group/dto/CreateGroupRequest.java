package com.hwsinha.carbid.group.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateGroupRequest(
        @NotNull Long carId,
        @NotBlank String city,
        @Min(2) int minMembers,
        @NotNull @Future Instant deadline,
        @NotNull Long createdById
        ) {
}
