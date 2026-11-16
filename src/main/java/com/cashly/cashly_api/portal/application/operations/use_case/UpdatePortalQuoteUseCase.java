package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.portal.application.dto.request.UpdatePortalQuoteRequest;
import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import com.cashly.cashly_api.sales.quotes.application.create.request.CreateQuoteRequest;
import com.cashly.cashly_api.sales.quotes.application.dto.response.QuoteDetailResponse;
import com.cashly.cashly_api.sales.quotes.application.update.request.UpdateQuoteRequest;
import com.cashly.cashly_api.sales.quotes.application.update.use_case.UpdateQuoteUseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class UpdatePortalQuoteUseCase {

    private final UpdateQuoteUseCase updateQuoteUseCase;
    private final GetPortalQuoteUseCase getPortalQuoteUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public UpdatePortalQuoteUseCase(UpdateQuoteUseCase updateQuoteUseCase,
            GetPortalQuoteUseCase getPortalQuoteUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        this.updateQuoteUseCase = updateQuoteUseCase;
        this.getPortalQuoteUseCase = getPortalQuoteUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional
    public QuoteDetailResponse execute(UUID id,
            UpdatePortalQuoteRequest request) {
        getPortalQuoteUseCase.execute(id);
        request.lines().forEach(line -> portalAuthorizationService
                .requireProductAccess(line.productId()));

        UpdateQuoteRequest internalRequest = new UpdateQuoteRequest(
                request.expiryDate(), request.notes(),
                request.lines().stream()
                        .map(line -> {
                            UUID warehouseId = portalAuthorizationService
                                    .resolveWarehouse(line.productId(),
                                            line.quantity(),
                                            line.warehouseId());
                            return new CreateQuoteRequest.QuoteLineRequest(
                                    line.productId(), line.quantity(),
                                    line.unitPrice(), List.of(
                                            new CreateQuoteRequest.WarehouseAllocationRequest(
                                                    warehouseId,
                                                    line.quantity())));
                        })
                        .toList());

        return updateQuoteUseCase.execute(id, internalRequest);
    }
}
