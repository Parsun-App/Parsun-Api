package com.parsun.parsun_api.partners.customers.profile.application.dto.response;

import com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.PaymentType;

public record CustomerListResponse(String id, Long code, String alias,
        String legalName, String rif, String type, boolean active,
        PaymentType paymentType, boolean deletable, boolean portalAccess) {

    public CustomerListResponse(String id, Long code, String alias,
            String legalName, String rif, String type, boolean active,
            PaymentType paymentType, boolean deletable) {
        this(id, code, alias, legalName, rif, type, active, paymentType,
                deletable, false);
    }
}
