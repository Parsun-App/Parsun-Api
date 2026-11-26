package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.partners.suppliers.domain.valueobjects.SupplierType;
import com.parsun.parsun_api.partners.suppliers.infrastructure.persistence.entities.SupplierAddressEntity;
import com.parsun.parsun_api.partners.suppliers.infrastructure.persistence.entities.SupplierEmailEntity;
import com.parsun.parsun_api.partners.suppliers.infrastructure.persistence.entities.SupplierEntity;
import com.parsun.parsun_api.partners.suppliers.infrastructure.persistence.entities.SupplierPhoneEntity;
import jakarta.persistence.EntityManager;
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
public class V106ValerySuppliersSeed implements SeedStep {

    private static final Pattern VALID_RIF = Pattern
            .compile("^[VJGECP]-\\d{8}-\\d$");
    private static final Pattern VALID_EMAIL = Pattern
            .compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1,
            1, 10, 0);

    private final EntityManager entityManager;

    public V106ValerySuppliersSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 106;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        Map<String, List<String[]>> addresses = groupAddresses();
        Set<Long> usedCodes = new HashSet<>(entityManager.createQuery(
                "select s.code from SupplierEntity s", Long.class)
                .getResultList());
        int sequence = 0;
        for (String[] row : ValeryCsv.load("suppliers")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            sequence++;
            UUID id = ValeryIds.supplier(code);
            if (entityManager.find(SupplierEntity.class, id) != null) {
                continue;
            }
            String name = ValeryCsv.orDefault(ValeryCsv.text(row, 1),
                    "PROVEEDOR " + code);
            var supplier = new SupplierEntity();
            supplier.setId(id);
            supplier.setCode(sequentialCode(sequence, usedCodes));
            supplier.setLegalName(ValeryCsv.trim(name, 200));
            supplier.setAlias(ValeryCsv.trim(name, 100));
            supplier.setType(supplierType(ValeryCsv.text(row, 2)));
            supplier.setRif(supplierRif(ValeryCsv.text(row, 2), code));
            supplier.setCreditDays(intValue(ValeryCsv.text(row, 9)));
            supplier.setCreditLimit(ValeryCsv.money(row, 8));
            supplier.setNotes("Importado de Valery");
            supplier.setActive(!"I".equalsIgnoreCase(ValeryCsv.text(row, 10)));
            supplier.setCreatedAt(CREATED_AT);
            supplier.setUpdatedAt(CREATED_AT);
            addEmail(supplier, code, ValeryCsv.text(row, 4));
            addPhone(supplier, code + ".p", ValeryCsv.text(row, 3));
            var main = new SupplierAddressEntity();
            main.setId(ValeryIds.supplierAddress(code, 0));
            main.setAddress(ValeryCsv.orDefault(ValeryCsv.text(row, 5),
                    "Sin direccion"));
            main.setBilling(true);
            main.setActive(true);
            main.setState(ValeryCsv.trim(ValeryCsv.text(row, 7), 100));
            main.setMunicipality("");
            supplier.getAddresses().add(main);
            int extra = 0;
            for (String[] address : addresses.getOrDefault(code, List.of())) {
                extra++;
                var entity = new SupplierAddressEntity();
                entity.setId(ValeryIds.supplierAddress(code, extra));
                entity.setAddress(ValeryCsv.orDefault(
                        ValeryCsv.text(address, 2), "Sin direccion"));
                entity.setBilling("S".equalsIgnoreCase(
                        ValeryCsv.text(address, 8)));
                entity.setActive(true);
                entity.setState(ValeryCsv.trim(
                        ValeryCsv.text(address, 4), 100));
                entity.setMunicipality(ValeryCsv.trim(
                        ValeryCsv.text(address, 5), 100));
                supplier.getAddresses().add(entity);
                addPhone(supplier, code + ".a" + extra,
                        ValeryCsv.text(address, 6));
            }
            entityManager.persist(supplier);
        }
    }

    private Map<String, List<String[]>> groupAddresses() {
        Map<String, List<String[]>> result = new HashMap<>();
        for (String[] row : ValeryCsv.load("supplier_addresses")) {
            String code = ValeryCsv.text(row, 0);
            result.computeIfAbsent(code, key -> new ArrayList<>()).add(row);
        }
        return result;
    }

    private void addEmail(SupplierEntity supplier, String code, String value) {
        String email = ValeryCsv.trim(value, 255).toLowerCase(Locale.ROOT);
        if (email.isEmpty() || !VALID_EMAIL.matcher(email).matches()) {
            return;
        }
        var entity = new SupplierEmailEntity();
        entity.setId(ValeryIds.supplierEmail(code));
        entity.setEmail(email);
        supplier.getEmails().add(entity);
    }

    private void addPhone(SupplierEntity supplier, String key, String value) {
        String phone = ValeryCsv.trim(value, 50);
        if (phone.isEmpty()) {
            return;
        }
        var entity = new SupplierPhoneEntity();
        entity.setId(ValeryIds.supplierPhone(key));
        entity.setPhone(phone);
        supplier.getPhones().add(entity);
    }

    private long sequentialCode(int sequence, Set<Long> used) {
        long candidate = sequence;
        while (!used.add(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private String supplierRif(String raw, String code) {
        String rif = ValeryCsv.trim(raw, 50).toUpperCase(Locale.ROOT);
        if (VALID_RIF.matcher(rif).matches()) {
            return rif;
        }
        long base = 0;
        for (char character : code.toCharArray()) {
            base = (base * 31 + character) % 100000000L;
        }
        return String.format("J-%08d-0", base);
    }

    private String supplierType(String rif) {
        String value = rif == null ? "" : rif.trim().toUpperCase(Locale.ROOT);
        boolean business = value.startsWith("J") || value.startsWith("G")
                || value.startsWith("E") || value.startsWith("P");
        return business ? SupplierType.JURIDICO.name()
                : SupplierType.NATURAL.name();
    }

    private int intValue(String raw) {
        try {
            return Integer.parseInt(raw.trim());
        } catch (RuntimeException exception) {
            return 0;
        }
    }
}
