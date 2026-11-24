package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.sales.orders.application.cancel.request.CancelOrderRequest;
import com.cashly.cashly_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.cashly.cashly_api.sales.orders.application.cancel.use_case.CancelOrderUseCase;
import com.cashly.cashly_api.portal.application.dto.request.CancelPortalOrderRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class CancelPortalOrderUseCase {

    private final CancelOrderUseCase cancelOrderUseCase;
    private final GetPortalOrderUseCase getPortalOrderUseCase;

    public CancelPortalOrderUseCase(CancelOrderUseCase cancelOrderUseCase,
                                    GetPortalOrderUseCase getPortalOrderUseCase) {
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.getPortalOrderUseCase = getPortalOrderUseCase;
    }

    @Transactional
    public OrderDetailResponse execute(UUID id, CancelPortalOrderRequest request) {
        getPortalOrderUseCase.execute(id);
        return cancelOrderUseCase.execute(id, new CancelOrderRequest(request.reason()));
    }
}
