package com.parsun.parsun_api.partners.customers.profile.application.ports.out;

public interface PortalPasswordEncoder {
    String encode(String rawPassword);
}
