package com.finance.dashboard.dto.user;

import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.UserStatus;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        UserStatus status
) {
}
