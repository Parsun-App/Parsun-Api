package com.cashly.cashly_api.portal.application.operations.use_case;

import com.cashly.cashly_api.partners.customers.profile.domain.entity.Customer;
import com.cashly.cashly_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.cashly.cashly_api.partners.customers.profile.domain.valueobjects.CustomerId;
import com.cashly.cashly_api.platform.notifications.application.services.NotificationService;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.create.request.CreatePaymentRequest;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.dto.response.PaymentResponse;
import com.cashly.cashly_api.finance.treasury.payments.collection.application.create.use_case.CreatePaymentUseCase;
import com.cashly.cashly_api.portal.application.dto.request.RegisterPortalPaymentRequest;
import com.cashly.cashly_api.portal.application.services.PortalAuthorizationService;
import com.cashly.cashly_api.shared.storage.domain.services.FileStorageService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public class RegisterPortalPaymentUseCase {

    private static final String PAYMENT_STATUS_PENDING = "PENDING";
    private static final String JUSTIFICANTE_PATH = "payments/justificantes/";

    private final CreatePaymentUseCase createPaymentUseCase;
    private final PortalAuthorizationService portalAuthorizationService;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;
    private final CustomerRepository customerRepository;

    public RegisterPortalPaymentUseCase(CreatePaymentUseCase createPaymentUseCase,
                                          PortalAuthorizationService portalAuthorizationService,
                                          FileStorageService fileStorageService,
                                          NotificationService notificationService,
                                          CustomerRepository customerRepository) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.portalAuthorizationService = portalAuthorizationService;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public PaymentResponse execute(RegisterPortalPaymentRequest request, MultipartFile justificante) {
        request.hasReferenceOrFile(justificante);
        UUID customerId = portalAuthorizationService.getCurrentCustomerId();

        String storedJustificantePath = null;
        if (justificante != null && !justificante.isEmpty()) {
            try {
                storedJustificantePath = fileStorageService.store(
                        JUSTIFICANTE_PATH + UUID.randomUUID() + "_" + justificante.getOriginalFilename(),
                        justificante.getInputStream(),
                        justificante.getContentType());
            } catch (IOException e) {
                throw new RuntimeException("Error storing justificante file", e);
            }
        }

        CreatePaymentRequest createRequest = new CreatePaymentRequest(
                customerId,
                PAYMENT_STATUS_PENDING,
                request.amount(),
                request.paymentMethodId(),
                request.referenceNumber(),
                request.paymentDate(),
                request.notes(),
                request.invoiceId(),
                storedJustificantePath
        );

        PaymentResponse response = createPaymentUseCase.execute(createRequest, null);

        String customerName = customerRepository
                .findById(new CustomerId(customerId))
                .map(c -> c.getLegalName() == null
                        ? null
                        : c.getLegalName().getValue())
                .orElse("Cliente");
        notificationService.notifyAdminsPaymentPending(response.id(), customerName, request.amount());

        return response;
    }
}
