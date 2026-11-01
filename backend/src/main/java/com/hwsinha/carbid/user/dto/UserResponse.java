package com.hwsinha.carbid.user.dto;

import com.hwsinha.carbid.user.Role;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        String city
) {
}
