package com.hwsinha.carbid.dealer.dto;

public record DealerResponse(
        long dealerId,
        long userId,
        String name,
        String email,
        String showroomName,
        String city
) {
}
