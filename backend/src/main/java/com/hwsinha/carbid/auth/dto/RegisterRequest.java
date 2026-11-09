package com.hwsinha.carbid.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Schema(example = "Peter Parker") @NotBlank String name,
        @Schema(example = "user1@carbid.com") @Email @NotBlank String email,
        @Schema(example = "pass12345") @NotBlank @Size(min = 8) String password,
        @Schema(example = "Mumbai") @NotBlank String city
) {
}
