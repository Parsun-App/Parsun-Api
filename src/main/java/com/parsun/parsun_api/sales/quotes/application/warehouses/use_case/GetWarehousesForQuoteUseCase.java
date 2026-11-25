package com.parsun.parsun_api.sales.quotes.application.warehouses.use_case;

import com.parsun.parsun_api.sales.quotes.application.ports.out.QuoteWarehouseGateway;
import com.parsun.parsun_api.sales.quotes.application.dto.response.QuoteWarehouseResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public class GetWarehousesForQuoteUseCase {

    private final QuoteWarehouseGateway warehouses;

    public GetWarehousesForQuoteUseCase(QuoteWarehouseGateway warehouses) {
        this.warehouses = warehouses;
    }

    @Transactional(readOnly = true)
    public List<QuoteWarehouseResponse> execute(UUID customerId) {
        return warehouses.accessibleWarehouseIds(customerId).stream()
                .map(warehouses::findById).flatMap(java.util.Optional::stream)
                .map(warehouse -> new QuoteWarehouseResponse(
                        warehouse.id().toString(), warehouse.name(),
                        warehouse.address()))
                .toList();
    }
}
