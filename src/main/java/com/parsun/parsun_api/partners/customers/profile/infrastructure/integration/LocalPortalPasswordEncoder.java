package com.parsun.parsun_api.partners.customers.profile.infrastructure.integration;

import com.parsun.parsun_api.partners.customers.profile.application.ports.out.PortalPasswordEncoder;
import com.parsun.parsun_api.platform.auth.user.domain.services.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LocalPortalPasswordEncoder implements PortalPasswordEncoder {

    private final PasswordEncoder encoder;

    public LocalPortalPasswordEncoder(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
