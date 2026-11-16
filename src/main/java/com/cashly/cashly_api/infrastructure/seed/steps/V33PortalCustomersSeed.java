package com.cashly.cashly_api.infrastructure.seed.steps;

import static com.cashly.cashly_api.infrastructure.seed.SeedIds.*;

import com.cashly.cashly_api.infrastructure.seed.SeedStep;
import com.cashly.cashly_api.partners.customers.profile.infrastructure.persistence.entities.CustomerAddressEntity;
import com.cashly.cashly_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEmailEntity;
import com.cashly.cashly_api.partners.customers.profile.infrastructure.persistence.entities.CustomerEntity;
import com.cashly.cashly_api.partners.customers.profile.infrastructure.persistence.entities.CustomerPhoneEntity;
import com.cashly.cashly_api.partners.customers.warehouse_access.infrastructure.persistence.entities.CustomerWarehouseAccessEntity;
import com.cashly.cashly_api.platform.auth.user.domain.services.PasswordEncoder;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Enables portal access for the deterministic seed customers and creates the
 * InvGlobal portal customer. The initial password is always the customer RIF.
 */
@Component
public class V33PortalCustomersSeed implements SeedStep {

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 1,
            3, 8, 0);
    private static final String GLOBAL_RIF = "J-99999999-9";

    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public V33PortalCustomersSeed(EntityManager entityManager,
            PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public int version() {
        return 33;
    }

    @Override
    public void seed() {
        enablePortal(CUSTOMER_ONE);
        enablePortal(CUSTOMER_TWO);
        createGlobalCustomer();
    }

    private void enablePortal(UUID customerId) {
        CustomerEntity customer = entityManager.find(CustomerEntity.class,
                customerId);
        if (customer == null) {
            return;
        }
        if (customer.getPortalPassword() == null
                || customer.getPortalPassword().isBlank()) {
            customer.setPortalAccess(true);
            customer.setPortalPassword(
                    passwordEncoder.encode(customer.getRif()));
            customer.setUpdatedAt(CREATED_AT);
            entityManager.merge(customer);
        }
    }

    private void createGlobalCustomer() {
        CustomerEntity customer = entityManager.find(CustomerEntity.class,
                CUSTOMER_GLOBAL);
        if (customer == null) {
            customer = new CustomerEntity();
            customer.setId(CUSTOMER_GLOBAL);
            customer.setCode(1011L);
            customer.setLegalName("Inversiones Global S.A.");
            customer.setType("BUSINESS");
            customer.setRif(GLOBAL_RIF);
            customer.setAlias("InvGlobal");
            customer.setCreditDays(30);
            customer.setCreditLimit(new BigDecimal("5000.00"));
            customer.setActive(true);
            customer.setCreatedAt(CREATED_AT);
            customer.setNotes("Deterministic portal seed customer");
            customer.setPriceTypeId(id("price-type.base"));

            customer.getEmails().add(email("customer-global-email.contact",
                    "contacto@inversionesglobal.com"));
            customer.getEmails().add(email("customer-global-email.management",
                    "gerencia@inversionesglobal.com"));
            customer.getPhones().add(phone("customer-global-phone.office",
                    "0212-7778899"));
            customer.getPhones().add(phone("customer-global-phone.mobile",
                    "0414-5566778"));
            customer.getAddresses().add(address("customer-global-address.main",
                    "Av. Andrés Bello, Torre Global, Piso 10, Caracas", true,
                    true, "Distrito Capital", "Libertador", "1050"));
            customer.getAddresses().add(address("customer-global-address.warehouse",
                    "Zona Industrial Los Cortijos, Galpón 4, Caracas", false,
                    true, "Miranda", "Sucre", "1070"));
        }

        customer.setPortalAccess(true);
        if (customer.getPortalPassword() == null
                || customer.getPortalPassword().isBlank()) {
            customer.setPortalPassword(passwordEncoder.encode(GLOBAL_RIF));
        }
        customer.setUpdatedAt(CREATED_AT);
        if (customer.getId() == null) {
            customer.setId(CUSTOMER_GLOBAL);
        }
        entityManager.merge(customer);
        ensureGlobalWarehouseAccess();
    }

    private CustomerEmailEntity email(String key, String value) {
        CustomerEmailEntity entity = new CustomerEmailEntity();
        entity.setId(id(key));
        entity.setCustomerId(CUSTOMER_GLOBAL);
        entity.setEmail(value);
        return entity;
    }

    private CustomerPhoneEntity phone(String key, String value) {
        CustomerPhoneEntity entity = new CustomerPhoneEntity();
        entity.setId(id(key));
        entity.setCustomerId(CUSTOMER_GLOBAL);
        entity.setPhone(value);
        return entity;
    }

    private CustomerAddressEntity address(String key, String value,
            boolean billing, boolean active, String state, String municipality,
            String postalCode) {
        CustomerAddressEntity entity = new CustomerAddressEntity();
        entity.setId(id(key));
        entity.setCustomerId(CUSTOMER_GLOBAL);
        entity.setAddress(value);
        entity.setBilling(billing);
        entity.setActive(active);
        entity.setState(state);
        entity.setMunicipality(municipality);
        entity.setPostalCode(postalCode);
        return entity;
    }

    private void ensureGlobalWarehouseAccess() {
        ensureWarehouseAccess("customer-global-main", WAREHOUSE_MAIN);
        ensureWarehouseAccess("customer-global-reserve", WAREHOUSE_RESERVE);
    }

    private void ensureWarehouseAccess(String key, UUID warehouseId) {
        UUID accessId = id("warehouse-access." + key);
        if (entityManager.find(CustomerWarehouseAccessEntity.class,
                accessId) != null) {
            return;
        }
        var access = new CustomerWarehouseAccessEntity();
        access.setId(accessId);
        access.setCustomerId(CUSTOMER_GLOBAL);
        access.setWarehouseId(warehouseId);
        access.setGrantedAt(CREATED_AT);
        access.setGrantedBy(ADMIN_USER);
        entityManager.persist(access);
    }
}
