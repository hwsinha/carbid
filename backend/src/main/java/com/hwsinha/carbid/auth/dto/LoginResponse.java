package com.hwsinha.carbid.auth.dto;

public record LoginResponse(
        String accessToken,
        String tokenType ,
        long  expiryInSeconds
) {
}

