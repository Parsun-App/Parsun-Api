package com.cashly.cashly_api.catalog.price_types.application.queries.use_case;

import com.cashly.cashly_api.catalog.price_types.application.dto.response.PriceTypeResponse;
import com.cashly.cashly_api.catalog.price_types.application.exceptions.PriceTypeNotFoundException;
import com.cashly.cashly_api.catalog.price_types.application.ports.out.PriceTypeUsageGateway;
import com.cashly.cashly_api.catalog.price_types.domain.repository.PriceTypeRepository;

import java.util.UUID;

public class GetPriceTypeUseCase {

    private final PriceTypeRepository priceTypeRepository;
    private final PriceTypeUsageGateway usageGateway;

    public GetPriceTypeUseCase(PriceTypeRepository priceTypeRepository,
            PriceTypeUsageGateway usageGateway) {
        this.priceTypeRepository = priceTypeRepository;
        this.usageGateway = usageGateway;
    }

    public PriceTypeResponse execute(UUID id) {
        var priceType = priceTypeRepository.findById(id)
                .orElseThrow(() -> new PriceTypeNotFoundException(id));
        boolean deletable = !usageGateway.isInUseByProducts(id)
                && !usageGateway.isInUseByCustomers(id);
        return PriceTypeResponse.from(priceType, deletable);
    }
}
