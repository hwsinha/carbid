package com.hwsinha.carbid.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "user1@carbid.com") @NotBlank @Email String email,
        @Schema(example = "pass12345") @NotBlank String password
) {
}
