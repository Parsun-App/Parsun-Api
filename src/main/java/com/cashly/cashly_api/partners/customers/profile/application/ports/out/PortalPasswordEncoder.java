package com.cashly.cashly_api.partners.customers.profile.application.ports.out;

public interface PortalPasswordEncoder {
    String encode(String rawPassword);
}
