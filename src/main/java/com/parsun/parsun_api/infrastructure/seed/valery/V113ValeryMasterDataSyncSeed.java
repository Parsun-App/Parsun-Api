package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.inventory.warehouses.management.infrastructure.persistence.entities.WarehouseEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerAddressEntity;
import com.parsun.parsun_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEntity;
import com.parsun.parsun_api.partners.suppliers.infrastructure.persistence.entities.SupplierEntity;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class V113ValeryMasterDataSyncSeed implements SeedStep {

    private static final Logger log = LoggerFactory
            .getLogger(V113ValeryMasterDataSyncSeed.class);

    private final EntityManager entityManager;

    public V113ValeryMasterDataSyncSeed(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public int version() {
        return 113;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        renumberCustomers();
        renumberSuppliers();
        updateWarehouses();
        completeCustomerAddresses();
    }

    private void renumberCustomers() {
        List<UUID> ids = new ArrayList<>();
        for (String[] row : ValeryCsv.load("customers")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            UUID id = ValeryIds.customer(code);
            if (entityManager.find(CustomerEntity.class, id) != null) {
                ids.add(id);
            }
        }
        renumber("CustomerEntity", ids);
        log.info("Valery customer codes synchronized: {}", ids.size());
    }

    private void renumberSuppliers() {
        List<UUID> ids = new ArrayList<>();
        for (String[] row : ValeryCsv.load("suppliers")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            UUID id = ValeryIds.supplier(code);
            if (entityManager.find(SupplierEntity.class, id) != null) {
                ids.add(id);
            }
        }
        renumber("SupplierEntity", ids);
        log.info("Valery supplier codes synchronized: {}", ids.size());
    }

    private void renumber(String entity, List<UUID> ids) {
        for (int index = 0; index < ids.size(); index++) {
            updateCode(entity, ids.get(index), -(index + 1L));
        }
        entityManager.flush();
        for (int index = 0; index < ids.size(); index++) {
            updateCode(entity, ids.get(index), index + 1L);
        }
        entityManager.flush();
    }

    private void updateCode(String entity, UUID id, long code) {
        entityManager.createQuery("update " + entity
                + " e set e.code = :code where e.id = :id")
                .setParameter("code", code).setParameter("id", id)
                .executeUpdate();
    }

    private void updateWarehouses() {
        for (String[] row : ValeryCsv.load("warehouses")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            WarehouseEntity warehouse = entityManager.find(
                    WarehouseEntity.class, ValeryIds.warehouse(code));
            if (warehouse == null) {
                continue;
            }
            warehouse.setAddress("Sector Centro, Municipio Tubores");
            warehouse.setState("Nueva Esparta");
            warehouse.setMunicipality("Tubores");
            warehouse.setPostalCode("6301");
        }
    }

    private void completeCustomerAddresses() {
        int updated = 0;
        int index = 0;
        for (String[] row : ValeryCsv.load("customers")) {
            String code = ValeryCsv.text(row, 0);
            if (code.isEmpty()) {
                continue;
            }
            index++;
            CustomerEntity customer = entityManager.find(CustomerEntity.class,
                    ValeryIds.customer(code));
            if (customer == null) {
                continue;
            }
            int addressIndex = 0;
            for (CustomerAddressEntity address : customer.getAddresses()) {
                addressIndex++;
                address.setAddress(V105ValeryCustomersSeed.resolveAddress(
                        address.getAddress(), index + addressIndex));
                address.setState("Nueva Esparta");
                address.setMunicipality("Tubores");
                address.setPostalCode("6301");
                updated++;
            }
        }
        log.info("Valery customer addresses completed: {}", updated);
    }
}
