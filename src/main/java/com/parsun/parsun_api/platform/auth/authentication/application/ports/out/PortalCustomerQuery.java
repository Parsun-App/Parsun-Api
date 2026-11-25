package com.parsun.parsun_api.platform.auth.authentication.application.ports.out;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PortalCustomerQuery {

    Optional<PortalCustomer> findByRif(String rif);

    Optional<PortalCustomer> findById(UUID id);

    boolean isPortalAccessEnabled(UUID id);

    record PortalCustomer(UUID id, String legalName, String rif, String email,
            String hashedPassword, boolean active, boolean portalAccess,
            LocalDateTime createdAt) {
    }
}
