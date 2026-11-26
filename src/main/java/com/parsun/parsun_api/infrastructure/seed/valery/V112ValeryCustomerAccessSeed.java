package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.infrastructure.seed.SeedIds;
import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerAddressEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEntity;
import com.parsun.parsun_api.partners.customers.warehouse_access.infrastructure.persistence.entities.CustomerWarehouseAccessEntity;
import com.parsun.parsun_api.inventory.warehouses.management.infrastructure.persistence.entities.WarehouseEntity;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class V112ValeryCustomerAccessSeed implements SeedStep {

    private static final Logger log = LoggerFactory
            .getLogger(V112ValeryCustomerAccessSeed.class);
    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1,
            1, 12, 0);

    private final EntityManager entityManager;

    public V112ValeryCustomerAccessSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 112;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        List<String> customerCodes = new ArrayList<>();
        for (String[] row : ValeryCsv.load("customers")) {
            String code = ValeryCsv.text(row, 0);
            if (!code.isEmpty()) {
                customerCodes.add(code);
            }
        }
        List<String> warehouseCodes = new ArrayList<>();
        for (String[] row : ValeryCsv.load("warehouses")) {
            String code = ValeryCsv.text(row, 0);
            if (!code.isEmpty()) {
                warehouseCodes.add(code);
            }
        }
        java.util.Set<String> existing = new java.util.HashSet<>();
        for (Object[] pair : entityManager.createQuery(
                "select a.customerId, a.warehouseId from CustomerWarehouseAccessEntity a",
                Object[].class).getResultList()) {
            existing.add(pair[0] + "|" + pair[1]);
        }
        int addresses = 0;
        int accesses = 0;
        int pending = 0;
        for (String customerCode : customerCodes) {
            UUID customerId = ValeryIds.customer(customerCode);
            CustomerEntity customer = entityManager.find(CustomerEntity.class,
                    customerId);
            if (customer == null) {
                continue;
            }
            if (ensureAddress(customerCode, customer)) {
                addresses++;
            }
            for (String warehouseCode : warehouseCodes) {
                UUID warehouseId = ValeryIds.warehouse(warehouseCode);
                if (entityManager.find(WarehouseEntity.class,
                        warehouseId) == null) {
                    continue;
                }
                String pairKey = customerId + "|" + warehouseId;
                if (!existing.add(pairKey)) {
                    continue;
                }
                var access = new CustomerWarehouseAccessEntity();
                access.setId(ValeryIds.customerWarehouse(customerCode,
                        warehouseCode));
                access.setCustomerId(customerId);
                access.setWarehouseId(warehouseId);
                access.setGrantedAt(CREATED_AT);
                access.setGrantedBy(SeedIds.ADMIN_USER);
                entityManager.persist(access);
                accesses++;
                pending++;
                if (pending % 500 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                    customer = entityManager.find(CustomerEntity.class,
                            customerId);
                }
            }
        }
        log.info("Valery customer addresses created: {}; warehouse accesses: {}",
                addresses, accesses);
    }

    private boolean ensureAddress(String customerCode, CustomerEntity customer) {
        boolean hasAddress = customer.getAddresses().stream()
                .anyMatch(address -> address.getAddress() != null
                        && !address.getAddress().isBlank());
        if (hasAddress) {
            return false;
        }
        var address = new CustomerAddressEntity();
        address.setId(ValeryIds.customerAddress(customerCode, 99));
        address.setAddress("Sin direccion");
        address.setBilling(true);
        address.setActive(true);
        address.setState("Nueva Esparta");
        address.setMunicipality("Tubores");
        address.setPostalCode("6301");
        customer.getAddresses().add(address);
        return true;
    }
}
