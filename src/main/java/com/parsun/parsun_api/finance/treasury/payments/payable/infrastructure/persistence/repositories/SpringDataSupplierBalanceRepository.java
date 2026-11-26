package com.parsun.parsun_api.finance.treasury.payments.payable.infrastructure.persistence.repositories;

import com.parsun.parsun_api.finance.treasury.payments.payable.infrastructure.persistence.entities.SupplierBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataSupplierBalanceRepository
        extends JpaRepository<SupplierBalanceEntity, UUID> {
    Optional<SupplierBalanceEntity> findBySupplierId(UUID supplierId);
}
