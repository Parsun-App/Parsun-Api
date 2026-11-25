package com.parsun.parsun_api.sales.quotes.application.cancel.use_case;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.platform.notifications.application.ports.in.CustomerNotificationRecorder;

import com.parsun.parsun_api.sales.quotes.application.cancel.request.CancelQuoteRequest;
import com.parsun.parsun_api.sales.quotes.domain.entity.Quote;
import com.parsun.parsun_api.sales.quotes.domain.exceptions.QuoteNotFoundException;
import com.parsun.parsun_api.sales.quotes.domain.repository.QuoteRepository;
import jakarta.transaction.Transactional;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class CancelQuoteUseCase {

    private final QuoteRepository quoteRepository;

    private final ActivityNotificationRecorder activityNotifications;
    private final CustomerNotificationRecorder customerNotifications;

    public CancelQuoteUseCase(QuoteRepository quoteRepository,
            ActivityNotificationRecorder activityNotifications) {
        this(quoteRepository, activityNotifications, null);
    }

    public CancelQuoteUseCase(QuoteRepository quoteRepository,
            ActivityNotificationRecorder activityNotifications,
            CustomerNotificationRecorder customerNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.quoteRepository = quoteRepository;
        this.customerNotifications = customerNotifications;
    }

    @Transactional
    @Audited(module = "sales", entityType = "QUOTE", action = "CANCEL")
    public void execute(UUID id, CancelQuoteRequest request) {
        Quote quote = quoteRepository.findById(id)
                .orElseThrow(() -> new QuoteNotFoundException(id));

        quote.cancel(request.reason());
        quoteRepository.save(quote);
        activityNotifications.record(ActivityEvent.of("SALES_QUOTES",
                "CANCEL", "QUOTE", quote.getId(),
                quote.getQuoteNumber().value(), null));
        if (customerNotifications != null) {
            customerNotifications.quoteCancelled(quote.getCustomerId(),
                    quote.getId(), request.reason());
        }
    }
}
