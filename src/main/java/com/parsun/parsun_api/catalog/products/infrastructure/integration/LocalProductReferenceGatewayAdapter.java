package com.parsun.parsun_api.catalog.products.infrastructure.integration;

import com.parsun.parsun_api.catalog.products.application.ports.out.ProductReferenceGateway;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
public class LocalProductReferenceGatewayAdapter
        implements
            ProductReferenceGateway {
    private final com.parsun.parsun_api.sales.orders.application.ports.in.ProductReferenceQuery orders;
    private final com.parsun.parsun_api.sales.invoices.invoices.application.ports.in.ProductReferenceQuery invoices;
    private final com.parsun.parsun_api.sales.invoices.returns.application.ports.in.ProductReferenceQuery returns;
    private final com.parsun.parsun_api.inventory.warehouses.stock.application.ports.in.ProductReferenceQuery warehouses;
    public LocalProductReferenceGatewayAdapter(
            com.parsun.parsun_api.sales.orders.application.ports.in.ProductReferenceQuery orders,
            com.parsun.parsun_api.sales.invoices.invoices.application.ports.in.ProductReferenceQuery invoices,
            com.parsun.parsun_api.sales.invoices.returns.application.ports.in.ProductReferenceQuery returns,
            com.parsun.parsun_api.inventory.warehouses.stock.application.ports.in.ProductReferenceQuery warehouses) {
        this.orders = orders;
        this.invoices = invoices;
        this.returns = returns;
        this.warehouses = warehouses;
    }
    @Override
    public Set<UUID> findReferencedProductIds(Set<UUID> ids) {
        if (ids.isEmpty()) {
            return Set.of();
        }
        Set<UUID> referenced = new HashSet<>(
                orders.findReferencedProductIds(ids));
        referenced.addAll(invoices.findReferencedProductIds(ids));
        referenced.addAll(returns.findReferencedProductIds(ids));
        referenced.addAll(warehouses.findReferencedProductIds(ids));
        return Set.copyOf(referenced);
    }
}
