package com.parsun.parsun_api.finance.treasury.receivables.application.payment_history.use_case;

import com.parsun.parsun_api.finance.treasury.receivables.application.dto.response.PendingPaymentResponse;
import com.parsun.parsun_api.finance.treasury.receivables.application.ports.out.ReceivablesQueryGateway;
import com.parsun.parsun_api.shared.dto.PageResponse;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetPaymentHistoryUseCase {

    private final ReceivablesQueryGateway receivablesRepository;

    public GetPaymentHistoryUseCase(
            ReceivablesQueryGateway receivablesRepository) {
        this.receivablesRepository = receivablesRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<PendingPaymentResponse> execute(String search, int page,
            int size) {
        PageResponse<PendingPaymentResponse> result = receivablesRepository
                .findPaymentHistory(search, page, size);
        return result;
    }

    @Transactional(readOnly = true)
    public PageResponse<PendingPaymentResponse> execute(UUID customerId,
            UUID invoiceId, int page, int size) {
        PageResponse<PendingPaymentResponse> result = receivablesRepository
                .findCustomerPaymentHistory(customerId, invoiceId, page, size);
        return result;
    }
}
