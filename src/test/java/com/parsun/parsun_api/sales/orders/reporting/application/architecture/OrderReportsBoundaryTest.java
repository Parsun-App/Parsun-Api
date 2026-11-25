package com.parsun.parsun_api.sales.orders.reporting.application.architecture;
import com.parsun.parsun_api.sales.orders.reporting.application.ports.out.OrderPdfPort;
import com.parsun.parsun_api.sales.orders.reporting.application.ports.out.OrdersReportPdfPort;
import com.parsun.parsun_api.sales.orders.reporting.application.generate_order_pdf.services.GenerateOrderPdfUseCaseImpl;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertFalse;
class OrderReportsBoundaryTest {
    @Test
    void applicationDoesNotDependOnForeignDtosRepositoriesOrAggregates() {
        for (var type : new Class<?>[]{OrderPdfPort.class,
                OrdersReportPdfPort.class, GenerateOrderPdfUseCaseImpl.class}) {
            Arrays.stream(type.getDeclaredFields()).forEach(
                    field -> assertFalse(field.getType().getName().matches(
                            ".*parsun_api\\.(orders\\.(?!reporting\\.)|warehouses\\.).*"),
                            field.toString()));
            Arrays.stream(type.getDeclaredMethods()).forEach(method -> Arrays
                    .stream(method.getGenericParameterTypes())
                    .forEach(parameter -> assertFalse(
                            parameter.getTypeName().matches(
                                    ".*parsun_api.sales.orders.(?!reporting\\.).*"),
                            parameter.toString())));
        }
    }
}