package com.parsun.parsun_api.finance.treasury.payments.payable.infrastructure.integration;

import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.movements.services.CashMovementService;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.repository.CashRegisterRepository;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.valueobjects.CashMovementDirection;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.domain.valueobjects.CashMovementType;
import com.parsun.parsun_api.finance.treasury.payments.payable.application.ports.out.SupplierBalanceCashGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Component
public class LocalSupplierBalanceCashGateway
        implements SupplierBalanceCashGateway {

    private final CashMovementService movements;
    private final CashRegisterRepository registers;

    public LocalSupplierBalanceCashGateway(CashMovementService movements,
            CashRegisterRepository registers) {
        this.movements = movements;
        this.registers = registers;
    }

    @Override
    public void requireOpenRegister() {
        movements.requireOpenRegister();
    }

    @Override
    public void record(String direction, String type, BigDecimal amount,
            UUID paymentMethodId, String description, String sourceType,
            UUID sourceId, UUID actorId, String actorName) {
        CashMovementDirection movementDirection = CashMovementDirection
                .valueOf(direction);
        CashMovementType movementType = CashMovementType.valueOf(type);
        LocalDate today = LocalDate.now();
        var register = registers.findByClosureDate(today);
        if (register.isPresent()) {
            movements.recordForRegister(register.get().getId(),
                    movementDirection, movementType, amount, paymentMethodId,
                    description, sourceType, sourceId, actorId, actorName,
                    today.atTime(LocalTime.NOON));
            return;
        }
        movements.requireOpenRegister();
        movements.record(movementDirection, movementType, amount,
                paymentMethodId, description, sourceType, sourceId, actorId,
                actorName);
    }
}
