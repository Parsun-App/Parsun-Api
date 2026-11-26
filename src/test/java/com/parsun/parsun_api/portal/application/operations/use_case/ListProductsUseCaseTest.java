package com.parsun.parsun_api.portal.application.operations.use_case;

import com.parsun.parsun_api.catalog.categories.domain.entity.Category;
import com.parsun.parsun_api.catalog.categories.domain.repository.CategoryRepository;
import com.parsun.parsun_api.catalog.products.domain.entity.Product;
import com.parsun.parsun_api.catalog.products.domain.repository.ProductRepository;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.entity.WarehouseStock;
import com.parsun.parsun_api.inventory.warehouses.stock.domain.repository.WarehouseStockRepository;
import com.parsun.parsun_api.partners.customers.profile.application.exceptions.CustomerNotFoundException;
import com.parsun.parsun_api.partners.customers.profile.domain.entity.Customer;
import com.parsun.parsun_api.partners.customers.profile.domain.repository.CustomerRepository;
import com.parsun.parsun_api.partners.customers.profile.domain.valueobjects.CustomerId;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.entity.CustomerWarehouseAccess;
import com.parsun.parsun_api.partners.customers.warehouse_access.domain.repository.CustomerWarehouseAccessRepository;
import com.parsun.parsun_api.portal.application.dto.response.PortalProductResponse;
import com.parsun.parsun_api.shared.dto.PageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Portal ListProductsUseCase Unit Tests")
class ListProductsUseCaseTest {

    private static final int PAGE = 0;
    private static final int PAGE_SIZE = 20;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerWarehouseAccessRepository customerWarehouseAccessRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private WarehouseStockRepository warehouseStockRepository;

    private ListProductsUseCase useCase;

    private final UUID customerId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID grantedBy = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        useCase = new ListProductsUseCase(customerRepository,
                customerWarehouseAccessRepository, productRepository,
                categoryRepository, warehouseStockRepository);
    }

    @Test
    @DisplayName("Sums available stock only for warehouses assigned to the customer")
    void shouldSumAvailableStockOnlyForAllowedWarehouses() {
        UUID allowedWarehouseId = UUID.randomUUID();
        UUID otherWarehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = product(productId);

        givenCustomer(customerWithWarehouses(allowedWarehouseId));
        PageResponse<Product> products = page(product);
        Set<UUID> allowedWarehouses = Set.of(allowedWarehouseId);
        when(productRepository.findAllForOrder(isNull(), isNull(), isNull(),
                eq(true), eq(allowedWarehouses), eq(PAGE), eq(PAGE_SIZE)))
                .thenReturn(products);
        when(categoryRepository.findAllById(anySet()))
                .thenReturn(List.of(category()));
        when(warehouseStockRepository.findAllByProductIdIn(Set.of(productId)))
                .thenReturn(List.of(
                        stock(allowedWarehouseId, productId, 10, 2),
                        stock(otherWarehouseId, productId, 100, 0)));

        PageResponse<PortalProductResponse> result = useCase.execute(
                customerId, null, null, PAGE, PAGE_SIZE);

        assertEquals(new BigDecimal("8"), result.content().get(0).availableStock());
        verify(productRepository).findAllForOrder(isNull(), isNull(), isNull(),
                eq(true), eq(allowedWarehouses), eq(PAGE), eq(PAGE_SIZE));
        verify(warehouseStockRepository)
                .findAllByProductIdIn(Set.of(productId));
    }

    @Test
    @DisplayName("Sums available stock across multiple allowed warehouses")
    void shouldSumAcrossMultipleAllowedWarehouses() {
        UUID firstWarehouseId = UUID.randomUUID();
        UUID secondWarehouseId = UUID.randomUUID();
        UUID otherWarehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = product(productId);

        givenCustomer(customerWithWarehouses(firstWarehouseId, secondWarehouseId));
        Set<UUID> allowedWarehouses = Set.of(firstWarehouseId, secondWarehouseId);
        when(productRepository.findAllForOrder(isNull(), isNull(), isNull(),
                eq(true), eq(allowedWarehouses), eq(PAGE), eq(PAGE_SIZE)))
                .thenReturn(page(product));
        when(categoryRepository.findAllById(anySet()))
                .thenReturn(List.of(category()));
        when(warehouseStockRepository.findAllByProductIdIn(Set.of(productId)))
                .thenReturn(List.of(
                        stock(firstWarehouseId, productId, 10, 2),
                        stock(secondWarehouseId, productId, 5, 0),
                        stock(otherWarehouseId, productId, 50, 0)));

        PageResponse<PortalProductResponse> result = useCase.execute(
                customerId, null, null, PAGE, PAGE_SIZE);

        assertEquals(new BigDecimal("13"), result.content().get(0).availableStock());
    }

    @Test
    @DisplayName("Returns zero stock when the product has stock only in non visible warehouses")
    void shouldReturnZeroStockWhenOnlyNonVisibleWarehouseHasStock() {
        UUID allowedWarehouseId = UUID.randomUUID();
        UUID otherWarehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = product(productId);

        givenCustomer(customerWithWarehouses(allowedWarehouseId));
        when(productRepository.findAllForOrder(isNull(), isNull(), isNull(),
                eq(true), eq(Set.of(allowedWarehouseId)), eq(PAGE),
                eq(PAGE_SIZE))).thenReturn(page(product));
        when(categoryRepository.findAllById(anySet()))
                .thenReturn(List.of(category()));
        when(warehouseStockRepository.findAllByProductIdIn(Set.of(productId)))
                .thenReturn(List.of(
                        stock(otherWarehouseId, productId, 50, 0)));

        PageResponse<PortalProductResponse> result = useCase.execute(
                customerId, null, null, PAGE, PAGE_SIZE);

        assertEquals(new BigDecimal("0"), result.content().get(0).availableStock());
    }

    @Test
    @DisplayName("Returns zero stock when the customer has no warehouse access")
    void shouldReturnZeroStockWhenCustomerHasNoWarehouseAccess() {
        UUID otherWarehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = product(productId);

        givenCustomer(customerWithWarehouses());
        when(productRepository.findAllForOrder(isNull(), isNull(), isNull(),
                eq(true), eq(Set.of()), eq(PAGE), eq(PAGE_SIZE)))
                .thenReturn(page(product));
        when(categoryRepository.findAllById(anySet()))
                .thenReturn(List.of(category()));
        when(warehouseStockRepository.findAllByProductIdIn(Set.of(productId)))
                .thenReturn(List.of(
                        stock(otherWarehouseId, productId, 50, 0)));

        PageResponse<PortalProductResponse> result = useCase.execute(
                customerId, null, null, PAGE, PAGE_SIZE);

        assertEquals(new BigDecimal("0"), result.content().get(0).availableStock());
    }

    @Test
    @DisplayName("Resolves the customer price for the mapped product")
    void shouldResolveCustomerPrice() {
        UUID allowedWarehouseId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Product product = product(productId);

        givenCustomer(customerWithWarehouses(allowedWarehouseId));
        when(productRepository.findAllForOrder(isNull(), isNull(), isNull(),
                eq(true), eq(Set.of(allowedWarehouseId)), eq(PAGE),
                eq(PAGE_SIZE))).thenReturn(page(product));
        when(categoryRepository.findAllById(anySet()))
                .thenReturn(List.of(category()));
        when(warehouseStockRepository.findAllByProductIdIn(Set.of(productId)))
                .thenReturn(List.of(
                        stock(allowedWarehouseId, productId, 4, 1)));

        PageResponse<PortalProductResponse> result = useCase.execute(
                customerId, null, null, PAGE, PAGE_SIZE);

        PortalProductResponse response = result.content().get(0);
        assertEquals(new BigDecimal("10.00"), response.price());
        assertEquals(new BigDecimal("3"), response.availableStock());
    }

    @Test
    @DisplayName("Throws when the customer does not exist")
    void shouldThrowWhenCustomerNotFound() {
        when(customerRepository.findById(new CustomerId(customerId)))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> useCase.execute(
                customerId, null, null, PAGE, PAGE_SIZE));
        verifyNoInteractions(productRepository, warehouseStockRepository);
    }

    private void givenCustomer(Customer customer) {
        when(customerRepository.findById(new CustomerId(customerId)))
                .thenReturn(Optional.of(customer));
    }

    private Customer customerWithWarehouses(UUID... warehouseIds) {
        Customer customer = mock(Customer.class);
        when(customer.getPriceTypeId()).thenReturn(null);
        List<CustomerWarehouseAccess> accesses = Arrays.stream(warehouseIds)
                .map(this::accessTo)
                .toList();
        when(customerWarehouseAccessRepository.findByCustomerId(customerId))
                .thenReturn(accesses);
        return customer;
    }

    private CustomerWarehouseAccess accessTo(UUID warehouseId) {
        return new CustomerWarehouseAccess(UUID.randomUUID(), customerId,
                warehouseId, LocalDateTime.now(), grantedBy);
    }

    private Product product(UUID productId) {
        LocalDateTime now = LocalDateTime.now();
        return Product.reconstitute(productId, "P-001", "Product",
                "Description", categoryId, false, true, null,
                new BigDecimal("10.00"), List.of(), List.of(), now, now);
    }

    private Category category() {
        LocalDateTime now = LocalDateTime.now();
        return Category.reconstitute(categoryId, "Category", "Description",
                true, now, now);
    }

    private WarehouseStock stock(UUID warehouseId, UUID productId, int quantity,
            int reserved) {
        LocalDateTime now = LocalDateTime.now();
        return new WarehouseStock(UUID.randomUUID(), warehouseId, productId,
                java.math.BigDecimal.valueOf(quantity),
                java.math.BigDecimal.valueOf(reserved), now, now);
    }

    private PageResponse<Product> page(Product... products) {
        List<Product> content = List.of(products);
        return new PageResponse<>(content, content.size(), 1, PAGE_SIZE, PAGE,
                true, true, content.isEmpty());
    }
}
