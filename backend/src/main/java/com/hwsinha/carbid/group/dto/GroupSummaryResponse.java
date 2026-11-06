package com.hwsinha.carbid.group.dto;

import com.hwsinha.carbid.group.GroupStatus;

import java.time.Instant;

public record GroupSummaryResponse(
        Long id,
        String brand,
        String model,
        String variant,
        String city,
        GroupStatus status,
        int minMembers,
        long memberCount,
        Instant deadline,
        Instant biddingDeadline
) {
}
