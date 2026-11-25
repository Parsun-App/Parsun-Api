package com.parsun.parsun_api.shared.infrastructure.security;

import com.parsun.parsun_api.platform.auth.authorization.application.services.AccessService;
import com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission;
import org.springframework.stereotype.Component;

@Component
public class AccessActiveTogglePermission implements ActiveTogglePermission {

    private final AccessService access;

    public AccessActiveTogglePermission(AccessService access) {
        this.access = access;
    }

    @Override
    public void requireActiveToggle() {
        access.requireActiveToggle();
    }
}
