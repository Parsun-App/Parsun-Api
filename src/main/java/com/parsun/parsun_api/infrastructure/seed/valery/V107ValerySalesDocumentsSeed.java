package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedIds;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceLineEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceNumberSequenceEntity;
import com.parsun.parsun_api.sales.orders.infrastructure.persistence.entities.OrderEntity;
import com.parsun.parsun_api.sales.orders.infrastructure.persistence.entities.OrderLineEntity;
import com.parsun.parsun_api.sales.orders.infrastructure.persistence.entities.OrderNumberSequenceEntity;
import com.parsun.parsun_api.sales.quotes.infrastructure.persistence.entities.QuoteJpaEntity;
import com.parsun.parsun_api.sales.quotes.infrastructure.persistence.entities.QuoteLineJpaEntity;
import com.parsun.parsun_api.sales.quotes.infrastructure.persistence.entities.QuoteNumberSequenceJpaEntity;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class V107ValerySalesDocumentsSeed implements SeedStep {

    private static final int YEAR = 2026;

    private final EntityManager entityManager;

    public V107ValerySalesDocumentsSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 107;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        usedNumbers.put("invoice", new java.util.HashSet<>(entityManager
                .createQuery("select i.invoiceNumber from InvoiceEntity i",
                        String.class).getResultList()));
        usedNumbers.put("order", new java.util.HashSet<>(entityManager
                .createQuery("select o.orderNumber from OrderEntity o",
                        String.class).getResultList()));
        usedNumbers.put("quote", new java.util.HashSet<>(entityManager
                .createQuery("select q.quoteNumber from QuoteJpaEntity q",
                        String.class).getResultList()));
        Map<String, List<String[]>> documentLines = lines("document_lines");
        int maxInvoice = invoices(documentLines);
        int maxOrder = orders(documentLines);
        int maxQuote = quotes(documentLines);
        sequence(InvoiceNumberSequenceEntity.class, maxInvoice);
        sequence(OrderNumberSequenceEntity.class, maxOrder);
        sequence(QuoteNumberSequenceJpaEntity.class, maxQuote);
    }

    private int invoices(Map<String, List<String[]>> lines) {
        int max = 0;
        for (String[] row : ValeryCsv.load("invoices")) {
            String key = ValeryCsv.text(row, 12);
            String document = ValeryCsv.text(row, 0);
            if (document.isEmpty()) {
                document = "S/N-" + key;
            }
            max = Math.max(max, numeric(key));
            UUID customerId = customerId(ValeryCsv.text(row, 1));
            if (customerId == null) {
                continue;
            }
            UUID id = ValeryIds.invoice(document + "#" + key);
            if (entityManager.find(InvoiceEntity.class, id) != null) {
                continue;
            }
            LocalDate issueDate = ValeryCsv.date(row, 4);
            if (issueDate == null) {
                issueDate = LocalDate.of(YEAR, 1, 1);
            }
            LocalDate dueDate = ValeryCsv.date(row, 5);
            LocalDateTime createdAt = issueDate.atTime(8, 0);
            var invoice = new InvoiceEntity();
            invoice.setId(id);
            invoice.setInvoiceNumber(uniqueNumber(document, key, "invoice"));
            invoice.setCustomerId(customerId);
            invoice.setCustomerName(ValeryCsv.trim(ValeryCsv.text(row, 2),
                    200));
            invoice.setCustomerRif(ValeryCsv.trim(ValeryCsv.text(row, 3), 50));
            invoice.setCreatedBy(SeedIds.ADMIN_USER);
            invoice.setCreatedByName("Parsun Administrator");
            invoice.setStatus(isCredit(row) ? "PENDING_PAYMENT" : "PAID");
            invoice.setPaymentType(isCredit(row) ? "CREDIT" : "CASH");
            invoice.setNotes("Importado de Valery");
            invoice.setSubtotal(ValeryCsv.money(row, 6));
            invoice.setReturnedTotal(BigDecimal.ZERO);
            invoice.setCreatedAt(createdAt);
            invoice.setUpdatedAt(createdAt);
            invoice.setIssueDate(issueDate);
            invoice.setDueDate(dueDate == null ? issueDate.plusDays(30)
                    : dueDate);
            int lineNumber = 0;
            for (String[] line : lines.getOrDefault(key, List.of())) {
                lineNumber++;
                invoice.getLines().add(invoiceLine(invoice, document,
                        lineNumber, line));
            }
            entityManager.persist(invoice);
            batch();
        }
        return max;
    }

    private InvoiceLineEntity invoiceLine(InvoiceEntity invoice,
            String document, int lineNumber, String[] row) {
        BigDecimal quantity = ValeryCsv.decimal(row, 4);
        BigDecimal price = ValeryCsv.money(row, 5);
        var line = new InvoiceLineEntity();
        line.setId(ValeryIds.invoiceLine(document, lineNumber));
        line.setInvoice(invoice);
        line.setProductId(productId(ValeryCsv.text(row, 2),
                ValeryCsv.text(row, 3)));
        line.setProductName(ValeryCsv.trim(ValeryCsv.text(row, 3), 200));
        line.setProductCode(ValeryCsv.trim(ValeryCsv.text(row, 2), 50));
        line.setQuantity(quantity);
        line.setUnitPrice(price);
        line.setLineTotal(ValeryCsv.money(row, 6));
        line.setReturnedQuantity(BigDecimal.ZERO);
        return line;
    }

    private int orders(Map<String, List<String[]>> lines) {
        int max = 0;
        for (String[] row : ValeryCsv.load("orders")) {
            String key = ValeryCsv.text(row, 7);
            String document = ValeryCsv.text(row, 0);
            if (document.isEmpty()) {
                document = "S/N-" + key;
            }
            max = Math.max(max, numeric(key));
            UUID customerId = customerId(ValeryCsv.text(row, 1));
            if (customerId == null) {
                continue;
            }
            UUID id = ValeryIds.order(document + "#" + key);
            if (entityManager.find(OrderEntity.class, id) != null) {
                continue;
            }
            LocalDate date = ValeryCsv.date(row, 4);
            LocalDateTime createdAt = (date == null
                    ? LocalDate.of(YEAR, 1, 1)
                    : date).atTime(8, 0);
            var order = new OrderEntity();
            order.setId(id);
            order.setOrderNumber(uniqueNumber(document, key, "order"));
            order.setCustomerId(customerId);
            order.setCustomerName(ValeryCsv.trim(ValeryCsv.text(row, 2),
                    200));
            order.setCustomerRif(ValeryCsv.trim(ValeryCsv.text(row, 3), 50));
            order.setCreatedBy(SeedIds.ADMIN_USER);
            order.setCreatedByName("Parsun Administrator");
            order.setStatus("CONFIRMED");
            order.setNotes("Importado de Valery");
            order.setSubtotal(ValeryCsv.money(row, 5));
            order.setCreatedAt(createdAt);
            order.setUpdatedAt(createdAt);
            int lineNumber = 0;
            for (String[] line : lines.getOrDefault(key, List.of())) {
                lineNumber++;
                order.getLines().add(orderLine(order, document, lineNumber,
                        line));
            }
            entityManager.persist(order);
            batch();
        }
        return max;
    }

    private OrderLineEntity orderLine(OrderEntity order, String document,
            int lineNumber, String[] row) {
        BigDecimal quantity = ValeryCsv.decimal(row, 4);
        BigDecimal price = ValeryCsv.money(row, 5);
        var line = new OrderLineEntity();
        line.setId(ValeryIds.orderLine(document, lineNumber));
        line.setOrder(order);
        line.setProductId(productId(ValeryCsv.text(row, 2),
                ValeryCsv.text(row, 3)));
        line.setProductName(ValeryCsv.trim(ValeryCsv.text(row, 3), 200));
        line.setProductCode(ValeryCsv.trim(ValeryCsv.text(row, 2), 50));
        line.setQuantity(quantity);
        line.setUnitPrice(price);
        line.setLineTotal(ValeryCsv.money(row, 6));
        return line;
    }

    private int quotes(Map<String, List<String[]>> lines) {
        int max = 0;
        for (String[] row : ValeryCsv.load("quotes")) {
            String key = ValeryCsv.text(row, 8);
            String document = ValeryCsv.text(row, 0);
            if (document.isEmpty()) {
                document = "S/N-" + key;
            }
            max = Math.max(max, numeric(key));
            UUID customerId = customerId(ValeryCsv.text(row, 1));
            if (customerId == null) {
                continue;
            }
            UUID id = ValeryIds.quote(document + "#" + key);
            if (entityManager.find(QuoteJpaEntity.class, id) != null) {
                continue;
            }
            LocalDate date = ValeryCsv.date(row, 4);
            if (date == null) {
                date = LocalDate.of(YEAR, 1, 1);
            }
            var quote = new QuoteJpaEntity();
            quote.setId(id);
            quote.setQuoteNumber(nextQuoteNumber(date.getYear()));
            quote.setCustomerId(customerId);
            quote.setCustomerName(ValeryCsv.trim(ValeryCsv.text(row, 2),
                    200));
            quote.setCustomerRif(ValeryCsv.trim(ValeryCsv.text(row, 3), 50));
            quote.setCreatedBy(SeedIds.ADMIN_USER);
            quote.setCreatedByName("Parsun Administrator");
            quote.setStatus("DRAFT");
            LocalDate expiry = ValeryCsv.date(row, 5);
            quote.setExpiryDate(expiry == null ? date.plusDays(30) : expiry);
            quote.setNotes("Importado de Valery");
            quote.setSubtotal(ValeryCsv.money(row, 6));
            quote.setCreatedAt(date.atStartOfDay().toInstant(ZoneOffset.UTC));
            quote.setUpdatedAt(date.atStartOfDay().toInstant(ZoneOffset.UTC));
            int lineNumber = 0;
            for (String[] line : lines.getOrDefault(key, List.of())) {
                lineNumber++;
                quote.getLines().add(quoteLine(quote, document, lineNumber,
                        line));
            }
            entityManager.persist(quote);
            batch();
        }
        return max;
    }

    private QuoteLineJpaEntity quoteLine(QuoteJpaEntity quote, String document,
            int lineNumber, String[] row) {
        BigDecimal quantity = ValeryCsv.decimal(row, 4);
        BigDecimal price = ValeryCsv.money(row, 5);
        var line = new QuoteLineJpaEntity();
        line.setId(ValeryIds.quoteLine(document, lineNumber));
        line.setQuote(quote);
        line.setProductId(productId(ValeryCsv.text(row, 2),
                ValeryCsv.text(row, 3)));
        line.setProductName(ValeryCsv.trim(ValeryCsv.text(row, 3), 200));
        line.setProductCode(ValeryCsv.trim(ValeryCsv.text(row, 2), 50));
        line.setQuantity(quantity);
        line.setUnitPrice(price);
        line.setLineTotal(ValeryCsv.money(row, 6));
        return line;
    }

    private final Map<String, java.util.Set<String>> usedNumbers = new HashMap<>();

    private String uniqueNumber(String document, String key, String kind) {
        var used = usedNumbers.computeIfAbsent(kind,
                value -> new java.util.HashSet<>());
        String number = ValeryCsv.trim(document, 50);
        if (!used.add(number)) {
            number = ValeryCsv.trim(document + "-" + key, 50);
            used.add(number);
        }
        return number;
    }

    private String nextQuoteNumber(int year) {
        var used = usedNumbers.computeIfAbsent("quote",
                value -> new java.util.HashSet<>());
        int sequence = 1;
        while (sequence <= 9999
                && used.contains(String.format("PRE-%d-%04d", year, sequence))) {
            sequence++;
        }
        String number = String.format("PRE-%d-%04d", year, sequence);
        used.add(number);
        return number;
    }

    private int pending;

    private void batch() {
        pending++;
        if (pending % 250 == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }

    private Map<String, List<String[]>> lines(String file) {
        Map<String, List<String[]>> result = new HashMap<>();
        for (String[] row : ValeryCsv.load(file)) {
            String document = ValeryCsv.text(row, 0);
            result.computeIfAbsent(document, key -> new ArrayList<>()).add(row);
        }
        return result;
    }

    private UUID customerId(String code) {
        UUID id = ValeryIds.customer(code == null ? "" : code.trim());
        if (entityManager.find(CustomerEntity.class, id) != null) {
            return id;
        }
        UUID generic = ValeryIds.customer("00");
        if (entityManager.find(CustomerEntity.class, generic) != null) {
            return generic;
        }
        return null;
    }

    private UUID productId(String code, String name) {
        String normalized = code == null ? "" : code.trim();
        if (normalized.isEmpty()) {
            normalized = "SIN-CODIGO-" + Math.abs(name == null ? 0
                    : name.hashCode());
        }
        UUID id = ValeryIds.product(normalized);
        if (entityManager.find(ProductEntity.class, id) != null) {
            return id;
        }
        var product = new ProductEntity();
        product.setId(id);
        product.setCode(ValeryCsv.trim(normalized, 50));
        product.setName(ValeryCsv.trim(ValeryCsv.orDefault(name, normalized),
                200));
        product.setDescription("Producto referenciado por documentos Valery");
        product.setComposite(false);
        product.setActive(false);
        product.setPurchasePrice(BigDecimal.ZERO);
        product.setCreatedAt(LocalDateTime.of(YEAR, 1, 1, 0, 0));
        product.setUpdatedAt(LocalDateTime.of(YEAR, 1, 1, 0, 0));
        entityManager.persist(product);
        return id;
    }

    private void sequence(Class<?> type, int max) {
        Object existing = entityManager.find(type, YEAR);
        if (existing == null) {
            try {
                var sequence = type.getDeclaredConstructor().newInstance();
                type.getMethod("setYear", Integer.class).invoke(sequence,
                        YEAR);
                type.getMethod("setNextValue", Integer.class).invoke(sequence,
                        Math.max(max + 1, 1));
                entityManager.persist(sequence);
            } catch (ReflectiveOperationException exception) {
                throw new IllegalStateException("Cannot seed number sequence",
                        exception);
            }
            return;
        }
        try {
            Integer current = (Integer) type.getMethod("getNextValue")
                    .invoke(existing);
            if (current == null || current <= max) {
                type.getMethod("setNextValue", Integer.class).invoke(existing,
                        max + 1);
            }
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot update number sequence",
                    exception);
        }
    }

    private boolean isCredit(String[] row) {
        return ValeryCsv.decimal(row, 9).signum() > 0;
    }

    private int numeric(String value) {
        String digits = value.replaceAll("[^0-9]", "");
        if (digits.isEmpty() || digits.length() > 9) {
            return 0;
        }
        return Integer.parseInt(digits);
    }
}
