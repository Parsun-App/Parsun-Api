package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.finance.treasury.payment_methods.infrastructure.persistence.entities.PaymentMethodEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V111ValeryPaymentMethodsSeed implements SeedStep {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1,
            1, 11, 0);

    private record Method(String key, String code, String name,
            String description) {
    }

    private static final List<Method> METHODS = List.of(
            new Method("cash", "CASH", "Efectivo",
                    "Pago en efectivo (Valery)"),
            new Method("transfer", "TRANSFER", "Transferencia",
                    "Transferencia bancaria (Valery)"),
            new Method("card", "CARD", "Tarjeta credito/debito",
                    "Tarjeta de credito o debito (Valery)"),
            new Method("mobile", "MOBILE_PAYMENT", "Pago movil",
                    "Pago movil bancario (Valery)"),
            new Method("check", "CHECK", "Cheque",
                    "Cheque bancario (Valery)"),
            new Method("deposit", "BANK_DEPOSIT", "Deposito bancario",
                    "Deposito bancario (Valery)"),
            new Method("foreign", "FOREIGN_CURRENCY", "Divisas",
                    "Pago en divisas (Valery)"),
            new Method("ticket", "FOOD_TICKET", "Cesta ticket",
                    "Cesta ticket (Valery)"),
            new Method("coupons", "COUPONS", "Cupones",
                    "Cupones de descuento (Valery)"));

    private final EntityManager entityManager;

    public V111ValeryPaymentMethodsSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 111;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        for (Method method : METHODS) {
            UUID id = ValeryIds.paymentMethod(method.key());
            if (entityManager.find(PaymentMethodEntity.class, id) != null) {
                continue;
            }
            var entity = new PaymentMethodEntity();
            entity.setId(id);
            entity.setCode(method.code());
            entity.setName(method.name());
            entity.setDescription(method.description());
            entity.setActive(true);
            entity.setCreatedAt(CREATED_AT);
            entity.setUpdatedAt(CREATED_AT);
            entityManager.persist(entity);
        }
    }
}
