package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.cashly.cashly_api.partners.customers.profile.domain.valueobjects.CustomerId;
import com.cashly.cashly_api.platform.notifications.application.services.NotificationService;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.create.request.CreatePaymentRequest;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.dto.response.PaymentResponse;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.create.use_case.CreatePaymentUseCase;
import com.cashly.cashly_api.portal.application.dto.request.PayPortalPartialDebtRequest;
import com.cashly.cashly_api.portal.application.services.DebtPaymentPlanService;
import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import com.cashly.cashly_api.shared.storage.domain.services.FileStorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PayPortalPartialDebtUseCase {

    private static final String STATUS_PENDING = "PENDING";
    private static final String JUSTIFICANTE_PATH = "payments/justificantes/";

    private final CreatePaymentUseCase createPaymentUseCase;
    private final DebtPaymentPlanService planService;
    private final PortalAuthorizationService portalAuthorizationService;
    private final NotificationService notificationService;
    private final CustomerRepository customerRepository;
    private final FileStorageService fileStorageService;

    public PayPortalPartialDebtUseCase(CreatePaymentUseCase createPaymentUseCase,
                                        DebtPaymentPlanService planService,
                                        PortalAuthorizationService portalAuthorizationService,
                                        NotificationService notificationService,
                                        CustomerRepository customerRepository,
                                        FileStorageService fileStorageService) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.planService = planService;
        this.portalAuthorizationService = portalAuthorizationService;
        this.notificationService = notificationService;
        this.customerRepository = customerRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public List<PaymentResponse> execute(PayPortalPartialDebtRequest request, MultipartFile justificante) {
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();
        String customerName = customerName(customerId);
        String voucherPath = storeVoucherIfPresent(justificante);

        BigDecimal amount = request.amount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero");
        }

        List<PaymentResponse> responses = planService.plan(customerId, amount)
                .stream()
                .map(plan -> createPayment(customerId, plan.invoiceId(),
                        plan.amount(), request, voucherPath))
                .toList();

        responses.forEach(response -> notificationService
                .notifyAdminsPaymentPending(response.id(), customerName,
                        response.amount()));
        return responses;
    }

    private PaymentResponse createPayment(UUID customerId, UUID invoiceId,
            BigDecimal amount, PayPortalPartialDebtRequest request,
            String voucherPath) {
        CreatePaymentRequest createRequest = new CreatePaymentRequest(
                customerId,
                STATUS_PENDING,
                amount,
                request.paymentMethodId(),
                request.referenceNumber(),
                request.paymentDate(),
                null,
                invoiceId,
                voucherPath
        );
        return createPaymentUseCase.execute(createRequest, null);
    }

    private String customerName(UUID customerId) {
        return customerRepository.findById(new CustomerId(customerId))
                .map(c -> c.getLegalName().getValue())
                .orElse("Cliente");
    }

    private String storeVoucherIfPresent(MultipartFile justificante) {
        if (justificante == null || justificante.isEmpty()) {
            return null;
        }
        try {
            return fileStorageService.store(
                    JUSTIFICANTE_PATH + UUID.randomUUID() + "_" + justificante.getOriginalFilename(),
                    justificante.getInputStream(),
                    justificante.getContentType());
        } catch (IOException e) {
            throw new RuntimeException("Error storing justificante file", e);
        }
    }
}
