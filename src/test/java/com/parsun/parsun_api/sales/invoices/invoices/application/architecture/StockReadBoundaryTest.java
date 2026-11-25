package com.parsun.parsun_api.sales.invoices.invoices.application.architecture;

import com.parsun.parsun_api.sales.invoices.invoices.application.get.use_case.GetInvoiceByIdUseCase;
import com.parsun.parsun_api.sales.invoices.invoices.application.update.use_case.UpdateInvoiceUseCase;

import com.parsun.parsun_api.sales.orders.application.get.use_case.GetOrderByIdUseCase;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.stream.Stream;
import static org.assertj.core.api.Assertions.assertThat;

class StockReadBoundaryTest {
    @Test
    void should_read_stock_through_owned_contracts() {
        assertThat(Stream
                .of(GetInvoiceByIdUseCase.class, UpdateInvoiceUseCase.class,
                        GetOrderByIdUseCase.class)
                .flatMap(type -> Arrays.stream(type.getDeclaredConstructors()))
                .flatMap(constructor -> Arrays
                        .stream(constructor.getParameterTypes()))
                .map(Class::getName))
                .noneMatch(name -> name.equals(
                        "com.parsun.parsun_api.shared.application.StockEnrichmentService"));
    }
}
