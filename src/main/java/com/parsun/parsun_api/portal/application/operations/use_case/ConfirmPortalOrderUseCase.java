package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.sales.orders.application.dto.response.OrderDetailResponse;
import com.parsun.parsun_api.sales.orders.application.confirm.use_case.ConfirmOrderUseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class ConfirmPortalOrderUseCase {

    private final ConfirmOrderUseCase confirmOrderUseCase;
    private final GetPortalOrderUseCase getPortalOrderUseCase;

    public ConfirmPortalOrderUseCase(ConfirmOrderUseCase confirmOrderUseCase,
                                    GetPortalOrderUseCase getPortalOrderUseCase) {
        this.confirmOrderUseCase = confirmOrderUseCase;
        this.getPortalOrderUseCase = getPortalOrderUseCase;
    }

    @Transactional
    public OrderDetailResponse execute(UUID id) {
        getPortalOrderUseCase.execute(id);
        return confirmOrderUseCase.execute(id);
    }
}
