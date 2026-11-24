package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import com.cashly.cashly_api.sales.quotes.application.dto.response.QuoteListResponse;
import com.cashly.cashly_api.sales.quotes.application.list.use_case.GetQuotesUseCase;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ListPortalQuotesUseCase {

    private final GetQuotesUseCase getQuotesUseCase;
    private final PortalAuthorizationService portalAuthorizationService;

    public ListPortalQuotesUseCase(GetQuotesUseCase getQuotesUseCase,
                                    PortalAuthorizationService portalAuthorizationService) {
        this.getQuotesUseCase = getQuotesUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
    }

    @Transactional(readOnly = true)
    public List<QuoteListResponse> execute(LocalDate dateFrom, LocalDate dateTo,
                                            String status, UUID productId,
                                            UUID categoryId, String orderBy,
                                            String orderDir) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String effectiveStatus = status == null || status.isBlank()
                ? null
                : status;
        return getQuotesUseCase.execute(dateFrom, dateTo, customerId,
                effectiveStatus, productId, categoryId, orderBy, orderDir);
    }
}
