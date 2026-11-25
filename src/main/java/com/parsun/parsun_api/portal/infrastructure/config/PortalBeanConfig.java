package com.parsun.parsun_api.portal.infrastructure.config;

import com.parsun.parsun_api.platform.auth.authentication.domain.services.AuthenticableResolver;
import com.parsun.parsun_api.platform.auth.authentication.infrastructure.integration.CustomerRifResolver;
import com.parsun.parsun_api.platform.auth.token.application.logout.use_case.LogoutUserUseCase;
import com.parsun.parsun_api.platform.auth.token.domain.repository.RefreshTokenRepository;
import com.parsun.parsun_api.platform.auth.token.domain.services.TokenService;
import com.parsun.parsun_api.platform.auth.user.domain.services.PasswordEncoder;
import com.parsun.parsun_api.catalog.categories.domain.repository.CategoryRepository;
import com.parsun.parsun_api.sales.invoices.credit_notes.domain.repository.CreditNoteRepository;
import com.parsun.parsun_api.partners.customers.balance.domain.repository.CustomerBalanceRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.repository.CustomerWarehouseAccessRepository;
import com.parsun.parsun_api.sales.invoices.debit_notes.domain.repository.DebitNoteRepository;
import com.parsun.parsun_api.platform.notifications.application.services.NotificationService;
import com.parsun.parsun_api.sales.invoices.invoices.domain.repository.InvoiceRepository;
import com.parsun.parsun_api.sales.invoices.invoices.application.get.use_case.GetInvoiceByIdUseCase;
import com.parsun.parsun_api.sales.invoices.invoices.application.list.use_case.GetInvoicesUseCase;
import com.parsun.parsun_api.sales.orders.application.cancel.use_case.CancelOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.confirm.use_case.ConfirmOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.create.use_case.CreateOrderUseCase;
import com.parsun.parsun_api.sales.orders.application.get.use_case.GetOrderByIdUseCase;
import com.parsun.parsun_api.sales.orders.application.list.use_case.GetOrdersUseCase;
import com.parsun.parsun_api.sales.orders.application.update.use_case.UpdateOrderUseCase;
import com.parsun.parsun_api.sales.orders.domain.repository.OrderRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.queries.use_case.GetAllPaymentsUseCase;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.queries.use_case.GetPaymentByIdUseCase;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.create.use_case.CreatePaymentUseCase;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.pay_customer_debt.use_case.PayAllCustomerDebtUseCase;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.pay_invoice.use_case.PayInvoiceUseCase;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.pay_partial_debt.use_case.PayPartialDebtUseCase;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentAllocationRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.repository.PaymentRepository;
import com.parsun.parsun_api.finance.treasury.payments.collection.application.ports.out.PaymentInvoiceGateway;
import com.parsun.parsun_api.finance.treasury.payments.collection.domain.services.PaymentAllocationService;
import com.parsun.parsun_api.portal.application.services.DebtPaymentPlanService;
import com.parsun.parsun_api.portal.application.operations.use_case.CancelPortalOrderUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.CancelPortalQuoteUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ConfirmPortalOrderUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ConvertPortalQuoteToOrderUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ChangeCustomerPasswordUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.CreatePortalOrderUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.CreatePortalQuoteUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GeneratePortalCatalogPdfUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GetPendingPortalPaymentsUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GetCustomerProfileUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GetPortalDashboardUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GetPortalInvoiceUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GetPortalOrderUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GetPortalPaymentUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.GetPortalQuoteUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ListPortalInvoicesUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ListPortalOrdersUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ListPortalPaymentsUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ListPortalQuotesUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ListProductsUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.UpdatePortalOrderUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.UpdatePortalQuoteUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.PayPortalAllDebtUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.PayPortalInvoiceUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.PayPortalPartialDebtUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.PortalLoginUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.RegisterPortalPaymentUseCase;
import com.parsun.parsun_api.portal.application.services.PortalAuthorizationService;
import com.parsun.parsun_api.catalog.products.domain.repository.ProductRepository;
import com.parsun.parsun_api.catalog.products.reporting.application.ports.out.CommercialCatalogPdfPort;
import com.parsun.parsun_api.shared.storage.domain.services.FileStorageService;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.repository.WarehouseStockRepository;
import com.parsun.parsun_api.sales.quotes.application.cancel.use_case.CancelQuoteUseCase;
import com.parsun.parsun_api.sales.quotes.application.convert_to_order.use_case.ConvertQuoteToOrderUseCase;
import com.parsun.parsun_api.sales.quotes.application.create.use_case.CreateQuoteUseCase;
import com.parsun.parsun_api.sales.quotes.application.get.use_case.GetQuoteByIdUseCase;
import com.parsun.parsun_api.sales.quotes.application.list.use_case.GetQuotesUseCase;
import com.parsun.parsun_api.sales.quotes.domain.repository.QuoteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortalBeanConfig {

    @Bean
    public PortalLoginUseCase portalLoginUseCase(
            CustomerRifResolver customerRifResolver,
            RefreshTokenRepository refreshTokenRepository,
            TokenService tokenService,
            PasswordEncoder passwordEncoder) {
        return new PortalLoginUseCase(
                customerRifResolver,
                refreshTokenRepository,
                tokenService,
                passwordEncoder);
    }

    @Bean
    public ChangeCustomerPasswordUseCase changeCustomerPasswordUseCase(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        return new ChangeCustomerPasswordUseCase(customerRepository, passwordEncoder);
    }

    @Bean
    public ListProductsUseCase portalListProductsUseCase(
            CustomerRepository customerRepository,
            CustomerWarehouseAccessRepository customerWarehouseAccessRepository,
            ProductRepository productRepository,
            CategoryRepository categoryRepository,
            WarehouseStockRepository warehouseStockRepository) {
        return new ListProductsUseCase(
                customerRepository,
                customerWarehouseAccessRepository,
                productRepository,
                categoryRepository,
                warehouseStockRepository);
    }

    @Bean
    public GeneratePortalCatalogPdfUseCase generatePortalCatalogPdfUseCase(
            ListProductsUseCase portalListProductsUseCase,
            CommercialCatalogPdfPort commercialCatalogPdfPort) {
        return new GeneratePortalCatalogPdfUseCase(
                portalListProductsUseCase, commercialCatalogPdfPort);
    }

    @Bean
    public GetCustomerProfileUseCase getCustomerProfileUseCase(
            CustomerRepository customerRepository,
            CustomerBalanceRepository customerBalanceRepository) {
        return new GetCustomerProfileUseCase(customerRepository, customerBalanceRepository);
    }

    @Bean
    public GetPortalDashboardUseCase getPortalDashboardUseCase(
            QuoteRepository quoteRepository,
            OrderRepository orderRepository,
            InvoiceRepository invoiceRepository,
            PaymentAllocationRepository paymentAllocationRepository,
            CreditNoteRepository creditNoteRepository,
            DebitNoteRepository debitNoteRepository,
            PortalAuthorizationService portalAuthorizationService) {
        return new GetPortalDashboardUseCase(
                quoteRepository,
                orderRepository,
                invoiceRepository,
                paymentAllocationRepository,
                creditNoteRepository,
                debitNoteRepository,
                portalAuthorizationService);
    }

    @Bean
    public ListPortalQuotesUseCase listPortalQuotesUseCase(
            GetQuotesUseCase getQuotesUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new ListPortalQuotesUseCase(getQuotesUseCase, portalAuthorizationService);
    }

    @Bean
    public GetPortalQuoteUseCase getPortalQuoteUseCase(
            GetQuoteByIdUseCase getQuoteByIdUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new GetPortalQuoteUseCase(getQuoteByIdUseCase, portalAuthorizationService);
    }

    @Bean
    public CreatePortalQuoteUseCase createPortalQuoteUseCase(
            CreateQuoteUseCase createQuoteUseCase,
            PortalAuthorizationService portalAuthorizationService,
            NotificationService notificationService) {
        return new CreatePortalQuoteUseCase(createQuoteUseCase, portalAuthorizationService, notificationService);
    }

    @Bean
    public CancelPortalQuoteUseCase cancelPortalQuoteUseCase(
            CancelQuoteUseCase cancelQuoteUseCase,
            GetPortalQuoteUseCase getPortalQuoteUseCase) {
        return new CancelPortalQuoteUseCase(cancelQuoteUseCase, getPortalQuoteUseCase);
    }

    @Bean
    public ConvertPortalQuoteToOrderUseCase convertPortalQuoteToOrderUseCase(
            ConvertQuoteToOrderUseCase convertQuoteToOrderUseCase,
            GetPortalQuoteUseCase getPortalQuoteUseCase) {
        return new ConvertPortalQuoteToOrderUseCase(convertQuoteToOrderUseCase, getPortalQuoteUseCase);
    }

    @Bean
    public ListPortalOrdersUseCase listPortalOrdersUseCase(
            GetOrdersUseCase getOrdersUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new ListPortalOrdersUseCase(getOrdersUseCase, portalAuthorizationService);
    }

    @Bean
    public GetPortalOrderUseCase getPortalOrderUseCase(
            GetOrderByIdUseCase getOrderByIdUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new GetPortalOrderUseCase(getOrderByIdUseCase, portalAuthorizationService);
    }

    @Bean
    public ConfirmPortalOrderUseCase confirmPortalOrderUseCase(
            ConfirmOrderUseCase confirmOrderUseCase,
            GetPortalOrderUseCase getPortalOrderUseCase) {
        return new ConfirmPortalOrderUseCase(confirmOrderUseCase, getPortalOrderUseCase);
    }

    @Bean
    public CancelPortalOrderUseCase cancelPortalOrderUseCase(
            CancelOrderUseCase cancelOrderUseCase,
            GetPortalOrderUseCase getPortalOrderUseCase) {
        return new CancelPortalOrderUseCase(cancelOrderUseCase, getPortalOrderUseCase);
    }

    @Bean
    public CreatePortalOrderUseCase createPortalOrderUseCase(
            CreateOrderUseCase createOrderUseCase,
            PortalAuthorizationService portalAuthorizationService,
            NotificationService notificationService) {
        return new CreatePortalOrderUseCase(createOrderUseCase, portalAuthorizationService, notificationService);
    }

    @Bean
    public UpdatePortalQuoteUseCase updatePortalQuoteUseCase(
            com.parsun.parsun_api.sales.quotes.application.update.use_case.UpdateQuoteUseCase updateQuoteUseCase,
            GetPortalQuoteUseCase getPortalQuoteUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new UpdatePortalQuoteUseCase(updateQuoteUseCase,
                getPortalQuoteUseCase, portalAuthorizationService);
    }

    @Bean
    public UpdatePortalOrderUseCase updatePortalOrderUseCase(
            UpdateOrderUseCase updateOrderUseCase,
            GetPortalOrderUseCase getPortalOrderUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new UpdatePortalOrderUseCase(updateOrderUseCase, getPortalOrderUseCase,
                portalAuthorizationService);
    }

    @Bean
    public ListPortalInvoicesUseCase listPortalInvoicesUseCase(
            GetInvoicesUseCase getInvoicesUseCase,
            PortalAuthorizationService portalAuthorizationService,
            PaymentRepository paymentRepository) {
        return new ListPortalInvoicesUseCase(getInvoicesUseCase, portalAuthorizationService, paymentRepository);
    }

    @Bean
    public GetPortalInvoiceUseCase getPortalInvoiceUseCase(
            GetInvoiceByIdUseCase getInvoiceByIdUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new GetPortalInvoiceUseCase(getInvoiceByIdUseCase, portalAuthorizationService);
    }

    @Bean
    public ListPortalPaymentsUseCase listPortalPaymentsUseCase(
            GetAllPaymentsUseCase getAllPaymentsUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new ListPortalPaymentsUseCase(getAllPaymentsUseCase, portalAuthorizationService);
    }

    @Bean
    public GetPortalPaymentUseCase getPortalPaymentUseCase(
            GetPaymentByIdUseCase getPaymentByIdUseCase,
            PortalAuthorizationService portalAuthorizationService) {
        return new GetPortalPaymentUseCase(getPaymentByIdUseCase, portalAuthorizationService);
    }

    @Bean
    public PayPortalInvoiceUseCase payPortalInvoiceUseCase(
            CreatePaymentUseCase createPaymentUseCase,
            GetPortalInvoiceUseCase getPortalInvoiceUseCase,
            PortalAuthorizationService portalAuthorizationService,
            NotificationService notificationService,
            FileStorageService fileStorageService) {
        return new PayPortalInvoiceUseCase(createPaymentUseCase, getPortalInvoiceUseCase, portalAuthorizationService,
                notificationService, fileStorageService);
    }

    @Bean
    public DebtPaymentPlanService debtPaymentPlanService(
            PaymentInvoiceGateway paymentInvoiceGateway,
            PaymentAllocationRepository paymentAllocationRepository,
            PaymentRepository paymentRepository,
            PaymentAllocationService paymentAllocationService) {
        return new DebtPaymentPlanService(paymentInvoiceGateway,
                paymentAllocationRepository, paymentRepository,
                paymentAllocationService);
    }

    @Bean
    public PayPortalAllDebtUseCase payPortalAllDebtUseCase(
            CreatePaymentUseCase createPaymentUseCase,
            DebtPaymentPlanService debtPaymentPlanService,
            PortalAuthorizationService portalAuthorizationService,
            NotificationService notificationService,
            CustomerRepository customerRepository,
            FileStorageService fileStorageService) {
        return new PayPortalAllDebtUseCase(createPaymentUseCase,
                debtPaymentPlanService, portalAuthorizationService,
                notificationService, customerRepository, fileStorageService);
    }

    @Bean
    public PayPortalPartialDebtUseCase payPortalPartialDebtUseCase(
            CreatePaymentUseCase createPaymentUseCase,
            DebtPaymentPlanService debtPaymentPlanService,
            PortalAuthorizationService portalAuthorizationService,
            NotificationService notificationService,
            CustomerRepository customerRepository,
            FileStorageService fileStorageService) {
        return new PayPortalPartialDebtUseCase(createPaymentUseCase,
                debtPaymentPlanService, portalAuthorizationService,
                notificationService, customerRepository, fileStorageService);
    }

    @Bean
    public GetPendingPortalPaymentsUseCase getPendingPortalPaymentsUseCase(
            PaymentRepository paymentRepository,
            PortalAuthorizationService portalAuthorizationService) {
        return new GetPendingPortalPaymentsUseCase(paymentRepository, portalAuthorizationService);
    }

    @Bean
    public RegisterPortalPaymentUseCase registerPortalPaymentUseCase(
            CreatePaymentUseCase createPaymentUseCase,
            PortalAuthorizationService portalAuthorizationService,
            FileStorageService fileStorageService,
            NotificationService notificationService,
            CustomerRepository customerRepository) {
        return new RegisterPortalPaymentUseCase(
                createPaymentUseCase, portalAuthorizationService, fileStorageService,
                notificationService, customerRepository);
    }
}
