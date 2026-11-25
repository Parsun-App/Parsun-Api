package com.parsun.parsun_api.partners.suppliers.domain.services;

import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.SupplierCode;

public interface SupplierCodeGenerator {
    SupplierCode generateNext();
}
