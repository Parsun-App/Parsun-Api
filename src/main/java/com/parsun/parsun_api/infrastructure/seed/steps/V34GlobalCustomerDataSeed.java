package com.parsun.parsun_api.infrastructure.seed.steps;

import static com.parsun.parsun_api.infrastructure.seed.SeedIds.*;

import com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.persistence.entities.PaymentAllocationEntity;
import com.parsun.parsun_api.finance.treasury.payments.collection.infrastructure.persistence.entities.PaymentEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceLineAllocationEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceLineEntity;
import com.parsun.parsun_api.sales.orders.infrastructure.persistence.entities.OrderEntity;
import com.parsun.parsun_api.sales.orders.infrastructure.persistence.entities.OrderLineAllocationEntity;
import com.parsun.parsun_api.sales.orders.infrastructure.persistence.entities.OrderLineEntity;
import com.parsun.parsun_api.sales.quotes.infrastructure.persistence.entities.QuoteAllocationJpaEntity;
import com.parsun.parsun_api.sales.quotes.infrastructure.persistence.entities.QuoteJpaEntity;
import com.parsun.parsun_api.sales.quotes.infrastructure.persistence.entities.QuoteLineJpaEntity;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Demo documents for the InvGlobal portal customer so the customer portal has
 * quotes, orders, invoices and payments to display.
 */
@Component
public class V34GlobalCustomerDataSeed implements SeedStep {

    private static final UUID QUOTE_CUSTOMER_DRAFT = id("global-quote.customer-draft");
    private static final UUID QUOTE_DRAFT = id("global-quote.draft");
    private static final UUID ORDER_CUSTOMER_DRAFT = id("global-order.customer-draft");
    private static final UUID ORDER_CONFIRMED = id("global-order.confirmed");
    private static final UUID ORDER_INVOICED = id("global-order.invoiced");
    private static final UUID INVOICE_PAID = id("global-invoice.paid");
    private static final UUID INVOICE_PENDING = id("global-invoice.pending");
    private static final UUID PAYMENT_APPLIED = id("global-payment.applied");
    private static final UUID PAYMENT_PENDING = id("global-payment.pending");

    private static final String CUSTOMER_NAME = "Inversiones Global S.A.";
    private static final String CUSTOMER_RIF = "J-99999999-9";
    private static final BigDecimal COFFEE_PRICE = new BigDecimal("6.50");
    private static final BigDecimal WATER_PRICE = new BigDecimal("1.00");

    private final EntityManager entityManager;

    public V34GlobalCustomerDataSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 34;
    }

    @Override
    public void seed() {
        customerDraftQuote();
        operatorDraftQuote();
        customerDraftOrder();
        confirmedOrder();
        invoicedOrderAndPaidInvoice();
        pendingInvoice();
        appliedPayment();
        pendingPayment();
    }

    private void customerDraftQuote() {
        Instant createdAt = Instant.parse("2026-11-10T09:00:00Z");
        quote(QUOTE_CUSTOMER_DRAFT, "PRE-2026-9001", "CUSTOMER_DRAFT",
                CUSTOMER_GLOBAL, CUSTOMER_NAME, "Cotización creada desde el portal",
                LocalDate.of(2026, 12, 15), createdAt, PRODUCT_COFFEE,
                "Ground coffee", "PRD-0001", new BigDecimal("3"), COFFEE_PRICE,
                false);
    }

    private void operatorDraftQuote() {
        Instant createdAt = Instant.parse("2026-11-05T10:00:00Z");
        quote(QUOTE_DRAFT, "PRE-2026-9002", "DRAFT", OPERATOR_USER,
                "Sales Operator", "Cotización preparada por ventas",
                LocalDate.of(2026, 12, 5), createdAt, PRODUCT_WATER,
                "Mineral water", "PRD-0002", new BigDecimal("5"), WATER_PRICE,
                true);
    }

    private void customerDraftOrder() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 12, 11, 0);
        order(ORDER_CUSTOMER_DRAFT, "ORD-2026-9001", "CUSTOMER_DRAFT",
                CUSTOMER_GLOBAL, CUSTOMER_NAME, "Pedido creado desde el portal",
                createdAt, PRODUCT_COFFEE, "Ground coffee", "PRD-0001",
                new BigDecimal("2"), COFFEE_PRICE, false);
    }

    private void confirmedOrder() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 8, 12, 0);
        order(ORDER_CONFIRMED, "ORD-2026-9002", "CONFIRMED", CUSTOMER_GLOBAL,
                CUSTOMER_NAME, "Pedido confirmado por ventas", createdAt,
                PRODUCT_WATER, "Mineral water", "PRD-0002",
                new BigDecimal("4"), WATER_PRICE, true);
    }

    private void invoicedOrderAndPaidInvoice() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 10, 20, 9, 0);
        LocalDate issueDate = LocalDate.of(2026, 10, 20);
        BigDecimal total = COFFEE_PRICE.multiply(new BigDecimal("10"));

        order(ORDER_INVOICED, "ORD-2026-9003", "INVOICED", CUSTOMER_GLOBAL,
                CUSTOMER_NAME, "Pedido pasado a factura", createdAt,
                PRODUCT_COFFEE, "Ground coffee", "PRD-0001",
                new BigDecimal("10"), COFFEE_PRICE, true);

        invoice(INVOICE_PAID, "INV-2026-9001", "PAID", "CREDIT", total,
                issueDate, issueDate.plusDays(30), createdAt, ORDER_INVOICED,
                "ORD-2026-9003", PRODUCT_COFFEE, "Ground coffee", "PRD-0001",
                new BigDecimal("10"), COFFEE_PRICE);
        setOrderInvoice(ORDER_INVOICED, INVOICE_PAID, "INV-2026-9001");
    }

    private void pendingInvoice() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 1, 9, 0);
        LocalDate issueDate = LocalDate.of(2026, 11, 1);
        BigDecimal total = COFFEE_PRICE.multiply(new BigDecimal("4"))
                .add(WATER_PRICE.multiply(new BigDecimal("4")));

        InvoiceEntity invoice = invoice(INVOICE_PENDING, "INV-2026-9002",
                "PENDING_PAYMENT", "CREDIT", total, issueDate,
                issueDate.plusDays(30), createdAt, null, null, PRODUCT_COFFEE,
                "Ground coffee", "PRD-0001", new BigDecimal("4"), COFFEE_PRICE);
        if (invoice == null) {
            invoice = entityManager.find(InvoiceEntity.class, INVOICE_PENDING);
        }
        if (invoice != null && invoice.getLines().size() == 1) {
            InvoiceLineEntity line = invoice.getLines().get(0);
            invoice.getLines().add(invoiceLine(invoice,
                    id("global-invoice-line.pending-water"), PRODUCT_WATER,
                    "Mineral water", "PRD-0002", new BigDecimal("4"),
                    WATER_PRICE));
            invoice.setSubtotal(total);
            entityManager.merge(invoice);
            allocateInvoice(invoice.getLines().get(1),
                    id("global-invoice-allocation.pending-water"),
                    new BigDecimal("4"));
        }
    }

    private void appliedPayment() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 15, 10, 0);
        BigDecimal amount = COFFEE_PRICE.multiply(new BigDecimal("10"));
        payment(PAYMENT_APPLIED, "PAY-2026-9001", amount, amount, "APPLIED",
                INVOICE_PAID, createdAt, "TRX-889900",
                "Pago de factura INV-2026-9001");
        allocatePayment(PAYMENT_APPLIED, INVOICE_PAID, amount, createdAt);
    }

    private void pendingPayment() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 11, 16, 10, 0);
        BigDecimal amount = new BigDecimal("15.00");
        payment(PAYMENT_PENDING, "PAY-2026-9002", amount, BigDecimal.ZERO,
                "PENDING", INVOICE_PENDING, createdAt, "TRX-PENDING-01",
                "Pago parcial reportado desde el portal");
    }

    private void quote(UUID id, String number, String status, UUID createdBy,
            String createdByName, String notes, LocalDate expiryDate,
            Instant createdAt, UUID productId, String productName,
            String productCode, BigDecimal quantity, BigDecimal unitPrice,
            boolean allocate) {
        if (entityManager.find(QuoteJpaEntity.class, id) != null) {
            return;
        }
        BigDecimal total = quantity.multiply(unitPrice);
        var quote = new QuoteJpaEntity();
        quote.setId(id);
        quote.setQuoteNumber(number);
        quote.setCustomerId(CUSTOMER_GLOBAL);
        quote.setCustomerName(CUSTOMER_NAME);
        quote.setCustomerRif(CUSTOMER_RIF);
        quote.setCreatedBy(createdBy);
        quote.setCreatedByName(createdByName);
        quote.setStatus(status);
        quote.setExpiryDate(expiryDate);
        quote.setNotes(notes);
        quote.setSubtotal(total);
        quote.setCreatedAt(createdAt);
        quote.setUpdatedAt(createdAt);

        var line = new QuoteLineJpaEntity();
        line.setId(id("global-quote-line." + number));
        line.setQuote(quote);
        line.setProductId(productId);
        line.setProductName(productName);
        line.setProductCode(productCode);
        line.setQuantity(quantity);
        line.setUnitPrice(unitPrice);
        line.setLineTotal(total);
        line.setCategorySnapshot(CATEGORY_DRINKS);

        if (allocate) {
            var allocation = new QuoteAllocationJpaEntity();
            allocation.setId(id("global-quote-allocation." + number));
            allocation.setQuoteLine(line);
            allocation.setQuoteId(id);
            allocation.setProductId(productId);
            allocation.setWarehouseId(WAREHOUSE_MAIN);
            allocation.setWarehouseName("Main warehouse");
            allocation.setAllocatedQuantity(quantity);
            line.getAllocations().add(allocation);
        }

        quote.getLines().add(line);
        entityManager.persist(quote);
    }

    private void order(UUID id, String number, String status, UUID createdBy,
            String createdByName, String notes, LocalDateTime createdAt,
            UUID productId, String productName, String productCode,
            BigDecimal quantity, BigDecimal unitPrice, boolean allocate) {
        if (entityManager.find(OrderEntity.class, id) != null) {
            return;
        }
        BigDecimal total = quantity.multiply(unitPrice);
        var order = new OrderEntity();
        order.setId(id);
        order.setOrderNumber(number);
        order.setCustomerId(CUSTOMER_GLOBAL);
        order.setCustomerName(CUSTOMER_NAME);
        order.setCustomerRif(CUSTOMER_RIF);
        order.setCreatedBy(createdBy);
        order.setCreatedByName(createdByName);
        order.setStatus(status);
        order.setNotes(notes);
        order.setSubtotal(total);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(createdAt);

        var line = new OrderLineEntity();
        line.setId(id("global-order-line." + number));
        line.setOrder(order);
        line.setProductId(productId);
        line.setProductName(productName);
        line.setProductCode(productCode);
        line.setQuantity(quantity);
        line.setUnitPrice(unitPrice);
        line.setLineTotal(total);
        line.setCategorySnapshot(CATEGORY_DRINKS);
        order.getLines().add(line);
        entityManager.persist(order);

        if (allocate) {
            allocateOrder(line, id("global-order-allocation." + number),
                    quantity);
        }
    }

    private void setOrderInvoice(UUID orderId, UUID invoiceId,
            String invoiceNumber) {
        OrderEntity order = entityManager.find(OrderEntity.class, orderId);
        if (order == null || order.getInvoiceId() != null) {
            return;
        }
        order.setInvoiceId(invoiceId);
        order.setInvoiceNumber(invoiceNumber);
        entityManager.merge(order);
    }

    private InvoiceEntity invoice(UUID id, String number, String status,
            String paymentType, BigDecimal total, LocalDate issueDate,
            LocalDate dueDate, LocalDateTime createdAt, UUID orderId,
            String orderNumber, UUID productId, String productName,
            String productCode, BigDecimal quantity, BigDecimal unitPrice) {
        if (entityManager.find(InvoiceEntity.class, id) != null) {
            return null;
        }
        var invoice = new InvoiceEntity();
        invoice.setId(id);
        invoice.setInvoiceNumber(number);
        invoice.setCustomerId(CUSTOMER_GLOBAL);
        invoice.setCustomerName(CUSTOMER_NAME);
        invoice.setCustomerRif(CUSTOMER_RIF);
        invoice.setCreatedBy(ADMIN_USER);
        invoice.setCreatedByName("Parsun Administrator");
        invoice.setOrderId(orderId);
        invoice.setOrderNumber(orderNumber);
        invoice.setStatus(status);
        invoice.setPaymentType(paymentType);
        invoice.setNotes("Factura de demostración del portal");
        invoice.setSubtotal(total);
        invoice.setReturnedTotal(BigDecimal.ZERO);
        invoice.setCreatedAt(createdAt);
        invoice.setUpdatedAt(createdAt);
        invoice.setIssueDate(issueDate);
        invoice.setDueDate(dueDate);
        invoice.getLines().add(invoiceLine(invoice,
                id("global-invoice-line." + number), productId, productName,
                productCode, quantity, unitPrice));
        entityManager.persist(invoice);
        allocateInvoice(invoice.getLines().get(0),
                id("global-invoice-allocation." + number), quantity);
        return invoice;
    }

    private InvoiceLineEntity invoiceLine(InvoiceEntity invoice, UUID id,
            UUID productId, String productName, String productCode,
            BigDecimal quantity, BigDecimal unitPrice) {
        var line = new InvoiceLineEntity();
        line.setId(id);
        line.setInvoice(invoice);
        line.setProductId(productId);
        line.setProductName(productName);
        line.setProductCode(productCode);
        line.setQuantity(quantity);
        line.setUnitPrice(unitPrice);
        line.setLineTotal(quantity.multiply(unitPrice));
        line.setReturnedQuantity(BigDecimal.ZERO);
        return line;
    }

    private void payment(UUID id, String number, BigDecimal amount,
            BigDecimal appliedAmount, String status, UUID targetInvoiceId,
            LocalDateTime createdAt, String reference, String notes) {
        if (entityManager.find(PaymentEntity.class, id) != null) {
            return;
        }
        var payment = new PaymentEntity();
        payment.setId(id);
        payment.setPaymentNumber(number);
        payment.setCustomerId(CUSTOMER_GLOBAL);
        payment.setAmount(amount);
        payment.setAppliedAmount(appliedAmount);
        payment.setPaymentMethodId(id("payment-method.transfer"));
        payment.setSource("PAYMENT_METHOD");
        payment.setReferenceNumber(reference);
        payment.setPaymentDate(createdAt.toLocalDate());
        payment.setStatus(status);
        payment.setNotes(notes);
        payment.setCreatedBy(ADMIN_USER);
        payment.setCreatedByName("Parsun Administrator");
        payment.setCreatedAt(createdAt);
        payment.setUpdatedAt(createdAt);
        payment.setTargetInvoiceId(targetInvoiceId);
        if ("APPLIED".equals(status)) {
            payment.setVerifiedBy(ADMIN_USER);
            payment.setVerifiedByName("Parsun Administrator");
            payment.setVerifiedAt(createdAt);
        }
        entityManager.persist(payment);
    }

    private void allocateOrder(OrderLineEntity line, UUID id,
            BigDecimal quantity) {
        if (entityManager.find(OrderLineAllocationEntity.class, id) != null) {
            return;
        }
        var allocation = new OrderLineAllocationEntity();
        allocation.setId(id);
        allocation.setOrderLine(line);
        allocation.setWarehouseId(WAREHOUSE_MAIN);
        allocation.setAllocated(quantity);
        entityManager.persist(allocation);
    }

    private void allocateInvoice(InvoiceLineEntity line, UUID id,
            BigDecimal quantity) {
        if (entityManager.find(InvoiceLineAllocationEntity.class, id) != null) {
            return;
        }
        var allocation = new InvoiceLineAllocationEntity();
        allocation.setId(id);
        allocation.setInvoiceLine(line);
        allocation.setWarehouseId(WAREHOUSE_MAIN);
        allocation.setAllocated(quantity);
        entityManager.persist(allocation);
    }

    private void allocatePayment(UUID paymentId, UUID invoiceId,
            BigDecimal amount, LocalDateTime createdAt) {
        UUID id = id("global-payment-allocation." + paymentId);
        if (entityManager.find(PaymentAllocationEntity.class, id) != null) {
            return;
        }
        var allocation = new PaymentAllocationEntity();
        allocation.setId(id);
        allocation.setPaymentId(paymentId);
        allocation.setInvoiceId(invoiceId);
        allocation.setAllocatedAmount(amount);
        allocation.setCreatedAt(createdAt);
        entityManager.persist(allocation);
    }
}
