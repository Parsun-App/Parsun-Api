package com.parsun.parsun_api.catalog.price_types.infrastructure.config;

import com.parsun.parsun_api.catalog.price_types.application.ports.in.ProductPriceTypeQuery;
import com.parsun.parsun_api.catalog.price_types.application.queries.services.RepositoryProductPriceTypeQuery;
import com.parsun.parsun_api.catalog.price_types.domain.repository.PriceTypeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductPriceTypeQueryConfig {
    @Bean
    public ProductPriceTypeQuery productPriceTypeQuery(
            PriceTypeRepository repository) {
        return new RepositoryProductPriceTypeQuery(repository);
    }
}
