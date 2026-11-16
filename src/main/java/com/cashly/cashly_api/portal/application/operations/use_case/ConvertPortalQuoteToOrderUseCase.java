package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.sales.quotes.application.convert_to_order.response.QuoteConvertedToOrderResponse;
import com.cashly.cashly_api.sales.quotes.application.convert_to_order.use_case.ConvertQuoteToOrderUseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class ConvertPortalQuoteToOrderUseCase {

    private final ConvertQuoteToOrderUseCase convertQuoteToOrderUseCase;
    private final GetPortalQuoteUseCase getPortalQuoteUseCase;

    public ConvertPortalQuoteToOrderUseCase(ConvertQuoteToOrderUseCase convertQuoteToOrderUseCase,
                                            GetPortalQuoteUseCase getPortalQuoteUseCase) {
        this.convertQuoteToOrderUseCase = convertQuoteToOrderUseCase;
        this.getPortalQuoteUseCase = getPortalQuoteUseCase;
    }

    @Transactional
    public QuoteConvertedToOrderResponse execute(UUID quoteId) {
        getPortalQuoteUseCase.execute(quoteId);
        return convertQuoteToOrderUseCase.execute(quoteId, null);
    }
}
