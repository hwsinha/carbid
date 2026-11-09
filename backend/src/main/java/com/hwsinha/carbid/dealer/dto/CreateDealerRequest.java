package com.hwsinha.carbid.dealer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateDealerRequest(
        @Schema(example = "Dealer Mumbai") @NotBlank String name,
        @Schema(example = "westernwheels@carbid.com") @NotBlank @Email String email,
        @Schema(example = "pass12345") @NotBlank @Size(min = 8) String password,
        @Schema(example = "Mumbai") @NotBlank String city,
        @Schema(example = "Western Wheels") @NotBlank String showroomName
) {
}
