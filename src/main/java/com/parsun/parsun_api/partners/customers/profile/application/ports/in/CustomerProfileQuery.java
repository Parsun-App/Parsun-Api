package com.parsun.parsun_api.partners.customers.profile.application.ports.in;

import java.util.UUID;

public interface CustomerProfileQuery {
    CustomerProfileSnapshot find(UUID customerId);
}
