package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.infrastructure.seed.SeedIds;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.partners.customers.balance.domain.valueobjects.BalanceType;
import com.parsun.parsun_api.partners.customers.balance.infrastructure.persistence.entities.BalanceTransactionEntity;
import com.parsun.parsun_api.partners.customers.balance.infrastructure.persistence.entities.BalanceTxSequenceEntity;
import com.parsun.parsun_api.partners.customers.balance.infrastructure.persistence.entities.CustomerBalanceEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEntity;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V108ValeryReceivablesSeed implements SeedStep {

    private static final int YEAR = 2026;

    private final EntityManager entityManager;

    public V108ValeryReceivablesSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 108;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        for (String[] row : ValeryCsv.load("receivables")) {
            String document = ValeryCsv.text(row, 0);
            BigDecimal balance = ValeryCsv.money(row, 9);
            if (document.isEmpty() || balance.signum() <= 0) {
                continue;
            }
            UUID customerId = customerId(ValeryCsv.text(row, 2));
            if (customerId == null) {
                continue;
            }
            UUID id = ValeryIds.balanceTransaction(document);
            if (entityManager.find(BalanceTransactionEntity.class,
                    id) != null) {
                continue;
            }
            LocalDate issueDate = ValeryCsv.date(row, 5);
            LocalDateTime createdAt = (issueDate == null
                    ? LocalDate.of(YEAR, 1, 1)
                    : issueDate).atTime(9, 0);
            var transaction = new BalanceTransactionEntity();
            transaction.setId(id);
            transaction.setCustomerId(customerId);
            transaction.setType(BalanceType.INVOICES.name());
            transaction.setAmount(balance);
            transaction.setReferenceType(ValeryCsv.orDefault(
                    ValeryCsv.text(row, 1), "FAC"));
            transaction.setReferenceNumber(ValeryCsv.trim(document, 100));
            transaction.setNumber(ValeryCsv.trim(document, 100));
            transaction.setCreatedBy(SeedIds.ADMIN_USER);
            transaction.setCreatedAt(createdAt);
            entityManager.persist(transaction);
            addBalance(ValeryCsv.text(row, 2), customerId, balance,
                    createdAt);
        }
        bumpSequence();
    }

    private void addBalance(String code, UUID customerId, BigDecimal amount,
            LocalDateTime createdAt) {
        UUID id = ValeryIds.customerBalance(code);
        CustomerBalanceEntity balance = entityManager.find(
                CustomerBalanceEntity.class, id);
        if (balance == null) {
            balance = new CustomerBalanceEntity();
            balance.setId(id);
            balance.setCustomerId(customerId);
            balance.setTotalBalance(BigDecimal.ZERO);
            balance.setCreatedAt(createdAt);
        }
        balance.setTotalBalance(balance.getTotalBalance().add(amount));
        balance.setUpdatedAt(createdAt);
        entityManager.persist(balance);
    }

    private void bumpSequence() {
        var sequence = entityManager.find(BalanceTxSequenceEntity.class, YEAR);
        int next = Math.max(entityManager.createQuery(
                "select count(b) from BalanceTransactionEntity b", Long.class)
                .getSingleResult().intValue() + 1, 1);
        if (sequence == null) {
            sequence = new BalanceTxSequenceEntity();
            sequence.setYear(YEAR);
            sequence.setNextValue(next);
            entityManager.persist(sequence);
        } else if (sequence.getNextValue() == null
                || sequence.getNextValue() < next) {
            sequence.setNextValue(next);
        }
    }

    private UUID customerId(String code) {
        UUID id = ValeryIds.customer(code == null ? "" : code.trim());
        if (entityManager.find(CustomerEntity.class, id) != null) {
            return id;
        }
        UUID generic = ValeryIds.customer("00");
        return entityManager.find(CustomerEntity.class, generic) != null
                ? generic
                : null;
    }
}
