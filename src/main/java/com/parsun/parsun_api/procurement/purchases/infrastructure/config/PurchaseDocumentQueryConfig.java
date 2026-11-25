package com.parsun.parsun_api.procurement.purchases.infrastructure.config;

import com.parsun.parsun_api.procurement.purchases.application.ports.in.PurchaseDocumentQuery;
import com.parsun.parsun_api.procurement.purchases.application.queries.services.PurchaseDocumentQueryService;
import com.parsun.parsun_api.procurement.purchases.application.list.use_case.ListPurchasesUseCase;
import com.parsun.parsun_api.procurement.purchases.domain.repository.PurchaseRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PurchaseDocumentQueryConfig {
    @Bean
    public PurchaseDocumentQuery purchaseDocumentQuery(
            PurchaseRepository repository, ListPurchasesUseCase listQuery) {
        return new PurchaseDocumentQueryService(repository, listQuery);
    }
}
