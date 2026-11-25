package com.parsun.parsun_api.finance.treasury.receivables.application.customer_debt.use_case;

import com.parsun.parsun_api.finance.treasury.receivables.application.dto.response.CustomerDebtSummaryResponse;
import com.parsun.parsun_api.finance.treasury.receivables.application.ports.out.ReceivablesQueryGateway;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetCustomerDebtUseCase {

    private final ReceivablesQueryGateway receivablesRepository;

    public GetCustomerDebtUseCase(
            ReceivablesQueryGateway receivablesRepository) {
        this.receivablesRepository = receivablesRepository;
    }

    @Transactional(readOnly = true)
    public CustomerDebtSummaryResponse execute(UUID customerId) {
        return receivablesRepository.findCustomerDebt(customerId);
    }
}
