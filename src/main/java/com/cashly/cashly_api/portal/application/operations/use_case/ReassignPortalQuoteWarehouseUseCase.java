package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import com.cashly.cashly_api.sales.quotes.application.create.request.CreateQuoteRequest;
import com.cashly.cashly_api.sales.quotes.application.dto.response.QuoteDetailResponse;
import com.cashly.cashly_api.sales.quotes.application.update.request.UpdateQuoteRequest;
import com.cashly.cashly_api.sales.quotes.application.update.use_case.UpdateQuoteUseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class ReassignPortalQuoteWarehouseUseCase {

    private final UpdateQuoteUseCase updateQuoteUseCase;
    private final GetPortalQuoteUseCase getPortalQuoteUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public ReassignPortalQuoteWarehouseUseCase(
            UpdateQuoteUseCase updateQuoteUseCase,
            GetPortalQuoteUseCase getPortalQuoteUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        this.updateQuoteUseCase = updateQuoteUseCase;
        this.getPortalQuoteUseCase = getPortalQuoteUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional
    public QuoteDetailResponse execute(UUID quoteId, UUID warehouseId) {
        QuoteDetailResponse quote = getPortalQuoteUseCase.execute(quoteId);
        portalAuthorizationService.requireWarehouseAccess(warehouseId);

        UpdateQuoteRequest request = new UpdateQuoteRequest(quote.expiryDate(),
                quote.notes(),
                quote.lines().stream()
                        .map(line -> new CreateQuoteRequest.QuoteLineRequest(
                                line.productId(), line.quantity(),
                                line.unitPrice(), List.of(
                                        new CreateQuoteRequest.WarehouseAllocationRequest(
                                                warehouseId,
                                                line.quantity()))))
                        .toList());
        return updateQuoteUseCase.execute(quoteId, request);
    }
}
