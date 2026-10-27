package com.hwsinha.carbid.group.dto;

import com.hwsinha.carbid.group.GroupStatus;

import java.time.Instant;

public record GroupResponse(
        Long id,
        Long carId,
        String city,
        GroupStatus status,
        int minMembers,
        Instant deadline
) {
}
