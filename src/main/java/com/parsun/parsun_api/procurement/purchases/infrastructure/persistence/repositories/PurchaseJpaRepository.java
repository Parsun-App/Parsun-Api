package com.parsun.parsun_api.procurement.purchases.infrastructure.persistence.repositories;

import com.parsun.parsun_api.procurement.purchases.infrastructure.persistence.entities.PurchaseEntity;
import com.parsun.parsun_api.procurement.purchases.infrastructure.persistence.entities.PurchaseLineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Collection;
import java.util.Set;

public interface PurchaseJpaRepository
        extends
            JpaRepository<PurchaseEntity, UUID> {

    @Query("""
            SELECT DISTINCT p FROM PurchaseEntity p
            LEFT JOIN FETCH p.lines
            WHERE p.id = :id
            """)
    Optional<PurchaseEntity> findWithLinesById(@Param("id") UUID id);

    @Query("""
            SELECT DISTINCT l FROM PurchaseLineEntity l
            LEFT JOIN FETCH l.allocations
            WHERE l.purchase.id = :purchaseId
            """)
    List<PurchaseLineEntity> findWithAllocationsForPurchase(
            @Param("purchaseId") UUID purchaseId);

    @Query("""
            SELECT DISTINCT p FROM PurchaseEntity p
            LEFT JOIN FETCH p.lines l
            WHERE (:dateFrom IS NULL OR p.createdAt >= :dateFrom)
            AND (:dateTo IS NULL OR p.createdAt <= :dateTo)
            AND (:supplierId IS NULL OR p.supplierId = :supplierId)
            AND p.status IN :statuses
            AND (:productId IS NULL OR EXISTS (
                SELECT 1 FROM PurchaseLineEntity pl WHERE pl.purchase = p AND pl.productId = :productId
            ))
            AND (:categoryId IS NULL OR EXISTS (
                SELECT 1 FROM PurchaseLineEntity pl2 WHERE pl2.purchase = p AND pl2.productId IN (
                    SELECT pr.id FROM com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity pr WHERE pr.categoryId = :categoryId
                )
            ))
            """)
    List<PurchaseEntity> findAllWithFilters(
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("supplierId") UUID supplierId,
            @Param("statuses") Collection<String> statuses,
            @Param("productId") UUID productId,
            @Param("categoryId") UUID categoryId);

    @Query(value = """
            SELECT p FROM PurchaseEntity p
            WHERE (:dateFrom IS NULL OR p.createdAt >= :dateFrom)
            AND (:dateTo IS NULL OR p.createdAt <= :dateTo)
            AND (:supplierId IS NULL OR p.supplierId = :supplierId)
            AND p.status IN :statuses
            AND (:productId IS NULL OR EXISTS (
                SELECT 1 FROM PurchaseLineEntity pl WHERE pl.purchase = p AND pl.productId = :productId
            ))
            AND (:categoryId IS NULL OR EXISTS (
                SELECT 1 FROM PurchaseLineEntity pl2 WHERE pl2.purchase = p AND pl2.productId IN (
                    SELECT pr.id FROM com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity pr WHERE pr.categoryId = :categoryId
                )
            ))
            AND (:search IS NULL OR :search = '' OR LOWER(p.purchaseNumber) LIKE :search
                OR LOWER(p.supplierName) LIKE :search)
            """, countQuery = """
            SELECT COUNT(p) FROM PurchaseEntity p
            WHERE (:dateFrom IS NULL OR p.createdAt >= :dateFrom)
            AND (:dateTo IS NULL OR p.createdAt <= :dateTo)
            AND (:supplierId IS NULL OR p.supplierId = :supplierId)
            AND p.status IN :statuses
            AND (:productId IS NULL OR EXISTS (
                SELECT 1 FROM PurchaseLineEntity pl WHERE pl.purchase = p AND pl.productId = :productId
            ))
            AND (:categoryId IS NULL OR EXISTS (
                SELECT 1 FROM PurchaseLineEntity pl2 WHERE pl2.purchase = p AND pl2.productId IN (
                    SELECT pr.id FROM com.parsun.parsun_api.catalog.products.infrastructure.persistence.entities.ProductEntity pr WHERE pr.categoryId = :categoryId
                )
            ))
            AND (:search IS NULL OR :search = '' OR LOWER(p.purchaseNumber) LIKE :search
                OR LOWER(p.supplierName) LIKE :search)
            """)
    Page<PurchaseEntity> findPageWithFilters(
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            @Param("supplierId") UUID supplierId,
            @Param("statuses") Collection<String> statuses,
            @Param("search") String search,
            @Param("productId") UUID productId,
            @Param("categoryId") UUID categoryId, Pageable pageable);

    @Query("""
            SELECT DISTINCT p FROM PurchaseEntity p
            LEFT JOIN FETCH p.lines
            WHERE p.id IN :ids
            """)
    List<PurchaseEntity> findAllWithLinesByIds(
            @Param("ids") Collection<UUID> ids);

    @Query(value = """
            SELECT EXISTS(
                SELECT 1 FROM purchases WHERE supplier_id = :supplierId
                UNION ALL SELECT 1 FROM supplier_payments WHERE supplier_id = :supplierId
                UNION ALL SELECT 1 FROM supplier_balance_transactions WHERE supplier_id = :supplierId
                UNION ALL SELECT 1 FROM supplier_balances WHERE supplier_id = :supplierId
            )
            """, nativeQuery = true)
    long existsBySupplierId(@Param("supplierId") UUID supplierId);

    @Query(value = """
            SELECT DISTINCT BIN_TO_UUID(x.supplier_id) FROM (
                SELECT supplier_id FROM purchases WHERE supplier_id IN :supplierIds
                UNION SELECT supplier_id FROM supplier_payments WHERE supplier_id IN :supplierIds
                UNION SELECT supplier_id FROM supplier_balance_transactions WHERE supplier_id IN :supplierIds
                UNION SELECT supplier_id FROM supplier_balances WHERE supplier_id IN :supplierIds
            ) x
            """, nativeQuery = true)
    Set<UUID> findSupplierIdsWithPurchases(
            @Param("supplierIds") Collection<UUID> supplierIds);
}
