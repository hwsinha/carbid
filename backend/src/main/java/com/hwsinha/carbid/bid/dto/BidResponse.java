package com.hwsinha.carbid.bid.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record BidResponse(
        Long id,
        Long groupId,
        Long dealerId,
        BigDecimal price,
        String notes,
        Instant createdAt
) {
}
