package com.parsun.parsun_api.platform.auth.department.domain.entity;

import java.util.UUID;
import com.parsun.parsun_api.platform.auth.user.domain.valueobjects.UserProfile;

public record Department(UUID id, String name, String description,
        boolean active, long version) {
    public Department {
        java.util.Objects.requireNonNull(id);
        name = UserProfile.text(name, 100, true);
        description = UserProfile.text(description, 500, false);
    }
}
