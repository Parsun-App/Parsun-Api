package com.parsun.parsun_api.partners.customers.balance.domain.repository;

import com.parsun.parsun_api.partners.customers.balance.domain.entity.BalanceTransaction;
import com.parsun.parsun_api.partners.customers.balance.domain.entity.CustomerBalance;
import com.parsun.parsun_api.shared.dto.PageResponse;

import java.util.Optional;
import java.util.UUID;

public interface CustomerBalanceRepository {

    CustomerBalance save(CustomerBalance balance);

    Optional<CustomerBalance> findByCustomerId(UUID customerId);

    BalanceTransaction saveTransaction(BalanceTransaction transaction);

    PageResponse<BalanceTransaction> findTransactionsByCustomerId(
            UUID customerId, int page, int size);

    long countTransactionsByCustomerId(UUID customerId);
}
