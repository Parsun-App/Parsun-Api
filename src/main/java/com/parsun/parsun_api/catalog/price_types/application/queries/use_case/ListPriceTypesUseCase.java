package com.parsun.parsun_api.catalog.price_types.application.queries.use_case;

import com.parsun.parsun_api.catalog.price_types.application.dto.response.PriceTypeResponse;
import com.parsun.parsun_api.catalog.price_types.domain.entity.PriceType;
import com.parsun.parsun_api.catalog.price_types.application.ports.out.PriceTypeUsageGateway;
import com.parsun.parsun_api.catalog.price_types.domain.repository.PriceTypeRepository;
import com.parsun.parsun_api.shared.dto.PageResponse;
import com.parsun.parsun_api.shared.domain.valueobjects.DocumentAction;
import com.parsun.parsun_api.shared.dto.PageResponses;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ListPriceTypesUseCase {

    private final PriceTypeUsageGateway usageGateway;
    private final PriceTypeRepository priceTypeRepository;

    public ListPriceTypesUseCase(PriceTypeUsageGateway usageGateway,
            PriceTypeRepository priceTypeRepository) {
        this.usageGateway = usageGateway;
        this.priceTypeRepository = priceTypeRepository;
    }

    public PageResponse<PriceTypeResponse> execute(String search,
            Boolean active, int page, int size, String orderBy,
            String orderDir) {
        PageResponse<PriceType> priceTypePage = priceTypeRepository
                .findAllWithFilters(search, active, page, size, orderBy,
                        orderDir);

        Set<UUID> priceTypeIds = priceTypePage.content().stream()
                .map(PriceType::getId).collect(Collectors.toSet());
        Set<UUID> inUseIds = new java.util.HashSet<>(
                usageGateway.findIdsInUseByProducts(priceTypeIds));
        inUseIds.addAll(usageGateway.findIdsInUseByCustomers(priceTypeIds));

        List<PriceTypeResponse> content = priceTypePage.content().stream()
                .map(pt -> PriceTypeResponse.from(pt,
                        !inUseIds.contains(pt.getId())))
                .toList();

        return new PageResponse<>(content, priceTypePage.totalElements(),
                priceTypePage.totalPages(), priceTypePage.size(),
                priceTypePage.number(), priceTypePage.first(),
                priceTypePage.last(), priceTypePage.empty());
    }

    public PageResponse<PriceTypeResponse> execute(String search, Boolean active, int page, int size, String orderBy, String orderDir, DocumentAction action) {
        if (action != DocumentAction.DELETE) return execute(search, active, page, size, orderBy, orderDir);
        var all = execute(search, active, 0, Integer.MAX_VALUE, orderBy, orderDir);
        return PageResponses.filterAndPage(all.content(), PriceTypeResponse::deletable, page, size);
    }
}
