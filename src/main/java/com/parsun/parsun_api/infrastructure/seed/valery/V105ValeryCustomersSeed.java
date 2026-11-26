package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.partners.customers.balance.infrastructure.persistence.entities.CustomerBalanceEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerAddressEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEmailEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerPhoneEntity;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class V105ValeryCustomersSeed implements SeedStep {

    private static final Pattern VALID_RIF = Pattern
            .compile("^[VJGECP]-\\d{8}-\\d$");
    private static final Pattern VALID_EMAIL = Pattern
            .compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1,
            1, 9, 0);

    private final EntityManager entityManager;

    public V105ValeryCustomersSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 105;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        Map<String, List<String[]>> addresses = groupAddresses();
        Set<Long> usedCodes = new HashSet<>(entityManager.createQuery(
                "select c.code from CustomerEntity c", Long.class)
                .getResultList());
        Set<String> usedRifs = new HashSet<>(entityManager.createQuery(
                "select c.rif from CustomerEntity c", String.class)
                .getResultList());
        int sequence = 0;
        for (String[] row : ValeryCsv.load("customers")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            sequence++;
            UUID id = ValeryIds.customer(code);
            if (entityManager.find(CustomerEntity.class, id) != null) {
                continue;
            }
            var customer = new CustomerEntity();
            customer.setId(id);
            customer.setCode(sequentialCode(sequence, usedCodes));
            String legalName = ValeryCsv.trim(ValeryCsv.text(row, 1), 200);
            if (legalName.length() < 2) {
                legalName = "CLIENTE " + code;
            }
            customer.setLegalName(legalName);
            customer.setAlias(ValeryCsv.trim(legalName, 100));
            customer.setType(customerType(ValeryCsv.text(row, 2)));
            customer.setRif(customerRif(ValeryCsv.text(row, 2), code,
                    usedRifs));
            customer.setCreditDays(intValue(ValeryCsv.text(row, 13)));
            customer.setCreditLimit(ValeryCsv.money(row, 12));
            customer.setActive(!"I".equalsIgnoreCase(ValeryCsv.text(row, 14)));
            customer.setPortalAccess(false);
            customer.setNotes("Importado de Valery");
            customer.setPriceTypeId(
                    ValeryIds.priceType(ValeryIds.PRICE_TYPE_MINIMO));
            customer.setCreatedAt(CREATED_AT);
            customer.setUpdatedAt(CREATED_AT);
            addEmail(customer, code + ".1", ValeryCsv.text(row, 5));
            addEmail(customer, code + ".2", ValeryCsv.text(row, 6));
            addPhones(customer, code, ValeryCsv.text(row, 3));
            addPhone(customer, code + ".m", ValeryCsv.text(row, 4));
            var main = new CustomerAddressEntity();
            main.setId(ValeryIds.customerAddress(code, 0));
            main.setAddress(ValeryCsv.orDefault(ValeryCsv.text(row, 7),
                    "Sin direccion"));
            main.setBilling(true);
            main.setActive(true);
            main.setState(defaultState(ValeryCsv.text(row, 9)));
            main.setMunicipality(defaultMunicipality(ValeryCsv.text(row, 10)));
            main.setPostalCode(defaultPostalCode(ValeryCsv.text(row, 11)));
            customer.getAddresses().add(main);
            int extra = 0;
            for (String[] address : addresses.getOrDefault(code, List.of())) {
                extra++;
                var entity = new CustomerAddressEntity();
                entity.setId(ValeryIds.customerAddress(code, extra));
                entity.setAddress(ValeryCsv.orDefault(
                        ValeryCsv.text(address, 3), "Sin direccion"));
                entity.setBilling("S".equalsIgnoreCase(
                        ValeryCsv.text(address, 9)));
                entity.setActive(true);
                entity.setState(defaultState(ValeryCsv.text(address, 5)));
                entity.setMunicipality(
                        defaultMunicipality(ValeryCsv.text(address, 6)));
                entity.setPostalCode("6301");
                customer.getAddresses().add(entity);
                addPhone(customer, code + ".a" + extra,
                        ValeryCsv.text(address, 7));
            }
            if (customer.getAddresses().isEmpty()) {
                var fallback = new CustomerAddressEntity();
                fallback.setId(ValeryIds.customerAddress(code, 99));
                fallback.setAddress("Sin direccion");
                fallback.setBilling(true);
                fallback.setActive(true);
                customer.getAddresses().add(fallback);
            }
            entityManager.persist(customer);
            persistBalance(code, id);
        }
    }

    private void persistBalance(String code, UUID customerId) {
        UUID balanceId = ValeryIds.customerBalance(code);
        if (entityManager.find(CustomerBalanceEntity.class, balanceId) != null) {
            return;
        }
        var balance = new CustomerBalanceEntity();
        balance.setId(balanceId);
        balance.setCustomerId(customerId);
        balance.setTotalBalance(BigDecimal.ZERO);
        balance.setCreatedAt(CREATED_AT);
        balance.setUpdatedAt(CREATED_AT);
        entityManager.persist(balance);
    }

    private Map<String, List<String[]>> groupAddresses() {
        Map<String, List<String[]>> result = new HashMap<>();
        for (String[] row : ValeryCsv.load("customer_addresses")) {
            String code = ValeryCsv.text(row, 0);
            result.computeIfAbsent(code, key -> new ArrayList<>()).add(row);
        }
        return result;
    }

    private void addEmail(CustomerEntity customer, String key, String value) {
        String email = ValeryCsv.trim(value, 255).toLowerCase(Locale.ROOT);
        if (email.isEmpty() || !VALID_EMAIL.matcher(email).matches()) {
            return;
        }
        var entity = new CustomerEmailEntity();
        entity.setId(ValeryIds.customerEmail(key, 0));
        entity.setEmail(email);
        customer.getEmails().add(entity);
    }

    private void addPhones(CustomerEntity customer, String code, String raw) {
        if (raw == null || raw.isBlank()) {
            return;
        }
        String[] parts = raw.split("[,;/|]");
        int index = 0;
        for (String part : parts) {
            index++;
            if (index > 3) {
                return;
            }
            addPhone(customer, code + ".p" + index, part);
        }
    }

    private void addPhone(CustomerEntity customer, String key, String value) {
        String phone = ValeryCsv.trim(value, 50);
        if (phone.isEmpty()) {
            return;
        }
        var entity = new CustomerPhoneEntity();
        entity.setId(ValeryIds.customerPhone(key, 0));
        entity.setPhone(phone);
        customer.getPhones().add(entity);
    }

    private long sequentialCode(int sequence, Set<Long> used) {
        long candidate = sequence;
        while (!used.add(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private String customerRif(String raw, String code, Set<String> used) {
        String rif = ValeryCsv.trim(raw, 50).toUpperCase(Locale.ROOT);
        if (!VALID_RIF.matcher(rif).matches()) {
            rif = syntheticRif(code, used);
        }
        if (!used.add(rif)) {
            rif = syntheticRif(code, used);
        }
        return rif;
    }

    private String syntheticRif(String code, Set<String> used) {
        long base = 0;
        for (char character : code.toCharArray()) {
            base = (base * 31 + character) % 100000000L;
        }
        for (int attempt = 0; attempt < 1000; attempt++) {
            String candidate = String.format("V-%08d-%d",
                    (base + attempt) % 100000000L, attempt % 10);
            if (!used.contains(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Cannot generate RIF for " + code);
    }

    private String customerType(String rif) {
        String value = rif == null ? "" : rif.trim().toUpperCase(Locale.ROOT);
        boolean business = value.startsWith("J") || value.startsWith("G")
                || value.startsWith("E") || value.startsWith("P");
        return business ? "BUSINESS" : "INDIVIDUAL";
    }

    private String defaultState(String raw) {
        String value = ValeryCsv.trim(raw, 100);
        return value.isEmpty() ? "Nueva Esparta" : value;
    }

    private String defaultMunicipality(String raw) {
        String value = ValeryCsv.trim(raw, 100);
        return value.isEmpty() ? "Tubores" : value;
    }

    private String defaultPostalCode(String raw) {
        String value = ValeryCsv.trim(raw, 10);
        return value.isEmpty() ? "6301" : value;
    }

    private int intValue(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (RuntimeException exception) {
            return 0;
        }
    }
}
