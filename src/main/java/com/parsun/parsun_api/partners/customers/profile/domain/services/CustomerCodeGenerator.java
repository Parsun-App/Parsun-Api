package com.parsun.parsun_api.partners.customers.profile.domain.services;

import com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.CustomerCode;

public interface CustomerCodeGenerator {
    CustomerCode generateNext();
}
