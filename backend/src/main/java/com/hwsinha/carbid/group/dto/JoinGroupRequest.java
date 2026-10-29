package com.hwsinha.carbid.group.dto;

import jakarta.validation.constraints.NotNull;

public record JoinGroupRequest(
        @NotNull Long userId
) {
}
