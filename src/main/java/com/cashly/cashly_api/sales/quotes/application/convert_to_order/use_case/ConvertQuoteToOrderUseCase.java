package com.cashly.cashly_api.sales.quotes.application.convert_to_order.use_case;

import com.cashly.cashly_api.platform.notifications.application.ports.in.ActivityEvent;

import com.cashly.cashly_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.cashly.cashly_api.platform.notifications.application.ports.in.CustomerNotificationRecorder;

import com.cashly.cashly_api.sales.quotes.application.ports.out.ActorIdentityGateway;
import com.cashly.cashly_api.sales.quotes.application.ports.out.OrderCreationGateway;
import com.cashly.cashly_api.sales.quotes.application.convert_to_order.response.QuoteConvertedToOrderResponse;
import com.cashly.cashly_api.sales.quotes.domain.exceptions.QuoteNotFoundException;
import com.cashly.cashly_api.sales.quotes.domain.repository.QuoteRepository;
import com.cashly.cashly_api.sales.quotes.domain.valueobjects.QuoteStatus;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import com.cashly.cashly_api.platform.audit.application.ports.in.Audited;

public class ConvertQuoteToOrderUseCase {
    private final QuoteRepository quotes;
    private final OrderCreationGateway orders;
    private final ActorIdentityGateway actors;

    private final ActivityNotificationRecorder activityNotifications;
    private final CustomerNotificationRecorder customerNotifications;

    public ConvertQuoteToOrderUseCase(QuoteRepository quotes,
            OrderCreationGateway orders, ActorIdentityGateway actors,
            ActivityNotificationRecorder activityNotifications) {
        this(quotes, orders, actors, activityNotifications, null);
    }

    public ConvertQuoteToOrderUseCase(QuoteRepository quotes,
            OrderCreationGateway orders, ActorIdentityGateway actors,
            ActivityNotificationRecorder activityNotifications,
            CustomerNotificationRecorder customerNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.quotes = quotes;
        this.orders = orders;
        this.actors = actors;
        this.customerNotifications = customerNotifications;
    }

    @Transactional
    @Audited(module = "sales", entityType = "QUOTE", action = "CONVERT_TO_ORDER")
    public QuoteConvertedToOrderResponse execute(UUID quoteId,
            UUID authenticatedUserId) {
        var quote = quotes.findById(quoteId)
                .orElseThrow(() -> new QuoteNotFoundException(quoteId));
        if (quote.getStatus() != QuoteStatus.DRAFT
                && quote.getStatus() != QuoteStatus.CUSTOMER_DRAFT) {
            throw new IllegalStateException(
                    "Only DRAFT quotes can be converted to orders");
        }
        var actor = authenticatedUserId == null
                ? null
                : actors.requireActor(authenticatedUserId);
        var lines = quote.getLines().stream()
                .map(line -> new OrderCreationGateway.Line(line.getProductId(),
                        line.getProductName(), line.getProductCode(),
                        line.getCategoryId(), line.getQuantity(),
                        line.getUnitPrice(),
                        line.getAllocations().stream().map(
                                allocation -> new OrderCreationGateway.Allocation(
                                        allocation.getWarehouseId(),
                                        allocation.getProductId(),
                                        allocation.getAllocatedQuantity()))
                                .toList()))
                .toList();
        var order = orders.create(new OrderCreationGateway.Command(
                quote.getCustomerId(), quote.getCustomerName(),
                quote.getCustomerRif(), actor == null ? null : actor.id(),
                actor == null ? null : actor.name(), lines));
        quote.markConvertedToOrder(order.id(), order.number());
        quotes.save(quote);
        activityNotifications.record(ActivityEvent.of("SALES_QUOTES",
                "CONVERT_TO_ORDER", "QUOTE", quote.getId(),
                quote.getQuoteNumber().value(), authenticatedUserId));
        if (customerNotifications != null) {
            customerNotifications.quoteConvertedToOrder(quote.getCustomerId(),
                    quote.getId(), order.number());
        }
        return new QuoteConvertedToOrderResponse(quote.getId(),
                quote.getQuoteNumber().value(), order.id(), order.number());
    }
}
