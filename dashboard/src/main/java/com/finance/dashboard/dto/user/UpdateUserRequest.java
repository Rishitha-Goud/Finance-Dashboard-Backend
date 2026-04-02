package com.finance.dashboard.dto.user;

import com.finance.dashboard.model.Role;
import com.finance.dashboard.model.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,
        @NotNull(message = "Role is required")
        Role role,
        @NotNull(message = "Status is required")
        UserStatus status
) {
}
