package com.parsun.parsun_api.sales.invoices.invoices.application.confirm.use_case;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.platform.notifications.application.ports.in.CustomerNotificationRecorder;

import com.parsun.parsun_api.sales.invoices.invoices.application.confirm.request.ConfirmInvoiceRequest;
import com.parsun.parsun_api.sales.invoices.invoices.application.dto.response.InvoiceDetailResponse;
import com.parsun.parsun_api.sales.invoices.invoices.application.mappers.InvoiceMapper;
import com.parsun.parsun_api.sales.invoices.invoices.application.issue.request.IssueInvoiceCommand;
import com.parsun.parsun_api.sales.invoices.invoices.application.issue.model.InvoiceOrigin;
import com.parsun.parsun_api.sales.invoices.invoices.application.issue.services.IssueInvoiceService;
import com.parsun.parsun_api.sales.invoices.invoices.domain.entity.Invoice;
import com.parsun.parsun_api.sales.invoices.invoices.domain.exceptions.InvoiceNotFoundException;
import com.parsun.parsun_api.sales.invoices.invoices.domain.repository.InvoiceRepository;
import com.parsun.parsun_api.sales.invoices.invoices.domain.valueobjects.InvoiceId;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

@Service
public class ConfirmInvoiceUseCase {

    private final InvoiceRepository invoiceRepository;
    private final IssueInvoiceService issuance;

    private final ActivityNotificationRecorder activityNotifications;
    private final CustomerNotificationRecorder customerNotifications;

    public ConfirmInvoiceUseCase(InvoiceRepository invoiceRepository,
            IssueInvoiceService issuance,
            ActivityNotificationRecorder activityNotifications) {
        this(invoiceRepository, issuance, activityNotifications, null);
    }

    @Autowired
    public ConfirmInvoiceUseCase(InvoiceRepository invoiceRepository,
            IssueInvoiceService issuance,
            ActivityNotificationRecorder activityNotifications,
            CustomerNotificationRecorder customerNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.invoiceRepository = invoiceRepository;
        this.issuance = issuance;
        this.customerNotifications = customerNotifications;
    }

    @Transactional
    @Audited(module = "sales", entityType = "INVOICE", action = "CONFIRM")
    public InvoiceDetailResponse execute(UUID id, ConfirmInvoiceRequest request,
            UUID authenticatedUserId) {
        Invoice invoice = invoiceRepository.findById(InvoiceId.of(id))
                .orElseThrow(() -> new InvoiceNotFoundException(id));
        Invoice saved = issuance.issue(invoice,
                new IssueInvoiceCommand(request.confirmationPassword(),
                        request.useBalance(), request.balanceAmount(),
                        request.paymentMethodId()),
                InvoiceOrigin.MANUAL_DRAFT, authenticatedUserId);
        activityNotifications.record(ActivityEvent.of("SALES_INVOICES_INVOICES",
                "CONFIRM", "INVOICE", saved.getId().value(),
                saved.getInvoiceNumber().value(), authenticatedUserId));
        if (customerNotifications != null) {
            customerNotifications.invoiceConfirmed(saved.getCustomerId(),
                    saved.getId().value(),
                    saved.getInvoiceNumber().value());
        }
        return InvoiceMapper.toDetailResponse(saved);
    }
}
