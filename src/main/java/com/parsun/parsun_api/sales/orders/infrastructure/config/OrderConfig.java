package com.parsun.parsun_api.sales.orders.infrastructure.config;

import com.parsun.parsun_api.sales.orders.application.ports.out.InvoiceCreationGateway;
import com.parsun.parsun_api.sales.orders.application.cancel.use_case.CancelOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.confirm.use_case.ConfirmOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.convert_to_invoice.use_case.ConvertToInvoiceUseCase;
import com.parsun.parsun_api.sales.orders.application.create.use_case.CreateOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.delete.use_case.DeleteOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.get.use_case.GetOrderByIdUseCase;
import com.parsun.parsun_api.sales.orders.application.list.use_case.GetOrdersUseCase;
import com.parsun.parsun_api.sales.orders.application.products.use_case.GetProductsForOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.update.use_case.UpdateOrderUseCase;
import com.parsun.parsun_api.sales.orders.domain.repository.OrderRepository;
import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.sales.orders.application.ports.out.StockAvailabilityGateway;
import com.parsun.parsun_api.sales.orders.application.ports.out.CustomerCreditGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderConfig {

    @Bean
    public CreateOrderUseCase createOrderUseCase(
            OrderRepository orderRepository,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderCustomerGateway customers,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderProductCatalogGateway products,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderActorGateway actors,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderInventoryGateway inventory,
            ActivityNotificationRecorder activityNotifications) {
        return new CreateOrderUseCase(orderRepository, customers, products,
                actors, inventory, activityNotifications);
    }

    @Bean
    public GetOrderByIdUseCase getOrderByIdUseCase(
            OrderRepository orderRepository,
            StockAvailabilityGateway stockEnrichmentService,
            CustomerCreditGateway creditGuard) {
        return new GetOrderByIdUseCase(orderRepository, stockEnrichmentService,
                creditGuard);
    }

    @Bean
    public GetOrdersUseCase getOrdersUseCase(OrderRepository orderRepository,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderProductCatalogGateway products) {
        return new GetOrdersUseCase(orderRepository, products);
    }

    @Bean
    public UpdateOrderUseCase updateOrderUseCase(
            OrderRepository orderRepository,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderProductCatalogGateway products,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderInventoryGateway inventory,
            ActivityNotificationRecorder activityNotifications) {
        return new UpdateOrderUseCase(orderRepository, products, inventory,
                activityNotifications);
    }

    @Bean
    public ConfirmOrderUseCase confirmOrderUseCase(
            OrderRepository orderRepository,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderInventoryGateway inventory,
            ActivityNotificationRecorder activityNotifications,
            com.parsun.parsun_api.platform.notifications.application.ports.in.CustomerNotificationRecorder customerNotifications) {
        return new ConfirmOrderUseCase(orderRepository, inventory,
                activityNotifications, customerNotifications);
    }

    @Bean
    public CancelOrderUseCase cancelOrderUseCase(
            OrderRepository orderRepository,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderInventoryGateway inventory,
            ActivityNotificationRecorder activityNotifications,
            com.parsun.parsun_api.platform.notifications.application.ports.in.CustomerNotificationRecorder customerNotifications) {
        return new CancelOrderUseCase(orderRepository, inventory,
                activityNotifications, customerNotifications);
    }

    @Bean
    public ConvertToInvoiceUseCase convertToInvoiceUseCase(
            OrderRepository orderRepository,
            InvoiceCreationGateway invoiceCreationGateway,
            ActivityNotificationRecorder activityNotifications,
            com.parsun.parsun_api.platform.notifications.application.ports.in.CustomerNotificationRecorder customerNotifications) {
        return new ConvertToInvoiceUseCase(orderRepository,
                invoiceCreationGateway, activityNotifications,
                customerNotifications);
    }

    @Bean
    public DeleteOrderUseCase deleteOrderUseCase(
            OrderRepository orderRepository,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderInventoryGateway inventory,
            ActivityNotificationRecorder activityNotifications) {
        return new DeleteOrderUseCase(orderRepository, inventory,
                activityNotifications);
    }

    @Bean
    public GetProductsForOrderUseCase getProductsForOrderUseCase(
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderCustomerGateway customers,
            com.parsun.parsun_api.sales.orders.application.ports.out.OrderProductCatalogGateway products) {
        return new GetProductsForOrderUseCase(customers, products);
    }
}
