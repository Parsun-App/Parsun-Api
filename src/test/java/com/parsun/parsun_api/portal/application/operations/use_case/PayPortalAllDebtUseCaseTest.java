package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.partners.customers.profile.domain.entity.Customer;
import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.CustomerId;
import com.parsun.parsun_api.platform.notifications.application.services.NotificationService;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.create.request.CreatePaymentRequest;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.dto.response.PaymentResponse;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.create.use_case.CreatePaymentUseCase;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.services.PaymentAllocationService;
import com.parsun.parsun_api.portal.application.dto.request.PayPortalDebtRequest;
import com.parsun.parsun_api.portal.application.services.DebtPaymentPlanService;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import com.parsun.parsun_api.shared.storage.domain.services.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("PayPortalAllDebtUseCase Unit Tests")
class PayPortalAllDebtUseCaseTest {

    @Mock
    private CreatePaymentUseCase createPaymentUseCase;

    @Mock
    private DebtPaymentPlanService planService;

    @Mock
    private PortalAuthorizationService portalAuthorizationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private FileStorageService fileStorageService;

    @Captor
    private ArgumentCaptor<CreatePaymentRequest> requestCaptor;

    private PayPortalAllDebtUseCase useCase;

    private final UUID customerId = UUID.randomUUID();
    private final UUID paymentId = UUID.randomUUID();
    private final UUID paymentMethodId = UUID.randomUUID();
    private final UUID firstInvoiceId = UUID.randomUUID();
    private final UUID secondInvoiceId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new PayPortalAllDebtUseCase(createPaymentUseCase, planService,
                portalAuthorizationService, notificationService,
                customerRepository, fileStorageService);
        when(portalAuthorizationService.getCurrentCustomerId()).thenReturn(customerId);
        Customer customer = mock(Customer.class);
        when(customer.getLegalName()).thenReturn(new com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.LegalName("Test Customer"));
        when(customerRepository.findById(new CustomerId(customerId))).thenReturn(Optional.of(customer));
        when(planService.outstanding(customerId)).thenReturn(new BigDecimal("500.00"));
        when(planService.plan(customerId, new BigDecimal("500.00"))).thenReturn(List.of(
                new PaymentAllocationService.AllocationPlan(firstInvoiceId,
                        new BigDecimal("300.00")),
                new PaymentAllocationService.AllocationPlan(secondInvoiceId,
                        new BigDecimal("200.00"))));
        when(createPaymentUseCase.execute(any(), isNull())).thenReturn(stubbedResponse());
    }

    private PayPortalDebtRequest request() {
        return new PayPortalDebtRequest(paymentMethodId, "REF-001", LocalDate.now(),
                new BigDecimal("500.00"), false, null);
    }

    private PaymentResponse stubbedResponse() {
        return new PaymentResponse(paymentId, "2026-0001", customerId, new BigDecimal("500.00"),
                paymentMethodId, "REF-001", LocalDate.now(), null, "PENDING", BigDecimal.ZERO,
                null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void should_CreateOnePaymentPerInvoice_When_PayingAllDebt() {
        useCase.execute(request(), null);

        verify(createPaymentUseCase, times(2)).execute(requestCaptor.capture(), isNull());
        assertEquals(firstInvoiceId, requestCaptor.getAllValues().get(0).targetInvoiceId());
        assertEquals(new BigDecimal("300.00"),
                requestCaptor.getAllValues().get(0).amount());
        assertEquals(secondInvoiceId, requestCaptor.getAllValues().get(1).targetInvoiceId());
        assertEquals(new BigDecimal("200.00"),
                requestCaptor.getAllValues().get(1).amount());
    }

    @Test
    void should_SetVoucherPath_When_JustificanteFileProvided() {
        MultipartFile justificante = new MockMultipartFile("justificante", "receipt.pdf",
                "application/pdf", "data".getBytes());
        when(fileStorageService.store(any(), any(), any())).thenReturn("payments/justificantes/stored.pdf");

        useCase.execute(request(), justificante);

        verify(createPaymentUseCase, times(2)).execute(requestCaptor.capture(), isNull());
        assertEquals("payments/justificantes/stored.pdf",
                requestCaptor.getAllValues().get(0).voucherPath());
    }

    @Test
    void should_LeaveVoucherPathNull_When_NoFileProvided() {
        useCase.execute(request(), null);

        verify(createPaymentUseCase, times(2)).execute(requestCaptor.capture(), isNull());
        assertNull(requestCaptor.getAllValues().get(0).voucherPath());
    }
}
