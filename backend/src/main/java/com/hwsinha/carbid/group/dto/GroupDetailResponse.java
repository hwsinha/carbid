package com.hwsinha.carbid.group.dto;

import com.hwsinha.carbid.group.GroupStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record GroupDetailResponse(
        Long id,
        String brand,
        String model,
        String variant,
        BigDecimal exShowroomPrice,
        String city, GroupStatus status,
        int minMembers, long memberCount,
        Instant deadline, Instant biddingDeadline,
        BigDecimal currentLowestPrice,
        BigDecimal winningPrice,
        String winningShowroom
) {
}
