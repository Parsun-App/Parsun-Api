package com.parsun.parsun_api.finance.treasury.cash_register.sessions.infrastructure.config;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.ports.in.CashRegisterOperations;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.services.CashRegisterOperationsService;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.movements.services.CashMovementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class CashRegisterOperationsConfig {
    @Bean
    public CashRegisterOperations cashRegisterOperations(
            CashMovementService movements) {
        return new CashRegisterOperationsService(movements);
    }
}
