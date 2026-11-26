package com.parsun.parsun_api.catalog.categories.infrastructure.config;

import com.parsun.parsun_api.catalog.categories.application.ports.in.ProductCategoryQuery;
import com.parsun.parsun_api.catalog.categories.application.queries.services.RepositoryProductCategoryQuery;
import com.parsun.parsun_api.catalog.categories.domain.repository.CategoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductCategoryQueryConfig {
    @Bean
    public ProductCategoryQuery productCategoryQuery(
            CategoryRepository repository) {
        return new RepositoryProductCategoryQuery(repository);
    }
}
