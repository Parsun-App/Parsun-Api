package com.parsun.parsun_api.inventory.warehouses.stock.infrastructure.integration;

import com.parsun.parsun_api.catalog.products.application.ports.in.StockCatalogQuery;
import com.parsun.parsun_api.catalog.products.application.exceptions.ProductNotFoundException;
import com.parsun.parsun_api.inventory.warehouses.stock.application.ports.out.StockCatalogGateway;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.Optional;

@Component
public class LocalStockCatalogGatewayAdapter implements StockCatalogGateway {
    private final StockCatalogQuery catalog;

    public LocalStockCatalogGatewayAdapter(StockCatalogQuery catalog) {
        this.catalog = catalog;
    }

    @Override
    public CatalogProduct getProduct(UUID productId) {
        return findProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    @Override
    public Optional<CatalogProduct> findProduct(UUID productId) {
        return catalog.findProduct(productId)
                .map(product -> toCatalogProduct(product));
    }

    private CatalogProduct toCatalogProduct(
            com.parsun.parsun_api.catalog.products.application.ports.in.StockCatalogEntry product) {
        return new CatalogProduct(product.id(), product.code(), product.name(),
                product.composite(),
                product.components().stream()
                        .map(component -> new Component(component.productId(),
                                component.quantity()))
                        .toList());
    }
}
