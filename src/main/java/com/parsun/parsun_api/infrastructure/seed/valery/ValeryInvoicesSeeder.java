package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity;
import com.parsun.parsun_api.infrastructure.seed.SeedIds;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceLineEntity;
import com.parsun.parsun_api.sales.invoices.invoices.infrastructure.persistence.entities.InvoiceNumberSequenceEntity;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ValeryInvoicesSeeder {

    private static final Logger log = LoggerFactory
            .getLogger(ValeryInvoicesSeeder.class);
    private static final int YEAR = 2026;

    private final EntityManager entityManager;
    private final Set<String> usedNumbers = new HashSet<>();
    private int pending;

    public ValeryInvoicesSeeder(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void seedInvoices() {
        usedNumbers.addAll(entityManager.createQuery(
                "select i.invoiceNumber from InvoiceEntity i", String.class)
                .getResultList());
        Map<String, List<String[]>> lines = lines("invoice_lines_only");
        invoices(lines);
        syncSequence();
    }

    private int invoices(Map<String, List<String[]>> lines) {
        int processed = 0;
        for (String[] row : ValeryCsv.load("invoices")) {
            String key = ValeryCsv.text(row, 12);
            String document = ValeryCsv.text(row, 0);
            if (document.isEmpty()) {
                document = "S/N-" + key;
            }
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
            invoice.setInvoiceNumber(uniqueNumber(document, key));
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
            processed++;
            pending++;
            if (pending % 250 == 0) {
                entityManager.flush();
                entityManager.clear();
            }
            if (processed % 1000 == 0) {
                log.info("Valery invoices progress: {}", processed);
            }
        }
        log.info("Valery invoices imported: {}", processed);
        return processed;
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

    private String uniqueNumber(String document, String key) {
        String number = ValeryCsv.trim(document, 50);
        if (!usedNumbers.add(number)) {
            number = ValeryCsv.trim(document + "-" + key, 50);
            usedNumbers.add(number);
        }
        return number;
    }

    private void syncSequence() {
        String prefixWithYear = "INV-" + YEAR + "-";
        int max = 0;
        for (String number : usedNumbers) {
            if (number == null || !number.startsWith(prefixWithYear)) {
                continue;
            }
            try {
                max = Math.max(max, Integer.parseInt(
                        number.substring(prefixWithYear.length())));
            } catch (NumberFormatException ignored) {
                max = max;
            }
        }
        int next = Math.min(max + 1, 9999);
        var existing = entityManager.find(InvoiceNumberSequenceEntity.class,
                YEAR);
        if (existing == null) {
            var sequence = new InvoiceNumberSequenceEntity();
            sequence.setYear(YEAR);
            sequence.setNextValue(next);
            entityManager.persist(sequence);
            return;
        }
        existing.setNextValue(next);
    }

    private Map<String, List<String[]>> lines(String file) {
        Map<String, List<String[]>> result = new HashMap<>();
        for (String[] row : ValeryCsv.load(file)) {
            result.computeIfAbsent(ValeryCsv.text(row, 0),
                    key -> new ArrayList<>()).add(row);
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

    private boolean isCredit(String[] row) {
        return ValeryCsv.decimal(row, 9).signum() > 0;
    }
}
