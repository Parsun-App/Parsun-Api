package com.parsun.parsun_api.portal.infrastructure.web;

import com.parsun.parsun_api.sales.invoices.invoices.application.dto.response.InvoiceDetailResponse;
import com.parsun.parsun_api.portal.application.dto.response.PortalInvoiceListResponse;
import com.parsun.parsun_api.portal.application.dto.response.PortalInvoiceSummaryResponse;
import com.parsun.parsun_api.portal.application.operations.use_case.GetPortalInvoiceUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.ListPortalInvoicesUseCase;
import com.parsun.parsun_api.shared.dto.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@PreAuthorize("hasAuthority('ROLE_PORTAL_CUSTOMER')")
@RequestMapping("/api/portal/invoices")
public class PortalInvoiceController {

    private final ListPortalInvoicesUseCase listPortalInvoicesUseCase;
    private final GetPortalInvoiceUseCase getPortalInvoiceUseCase;

    public PortalInvoiceController(ListPortalInvoicesUseCase listPortalInvoicesUseCase,
                                    GetPortalInvoiceUseCase getPortalInvoiceUseCase) {
        this.listPortalInvoicesUseCase = listPortalInvoicesUseCase;
        this.getPortalInvoiceUseCase = getPortalInvoiceUseCase;
    }

    @GetMapping
    public ResponseEntity<PageResponse<PortalInvoiceListResponse>> listInvoices(
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) List<String> statuses,
            @RequestParam(required = false) List<String> returnStatus,
            @RequestParam(required = false) UUID productId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String orderDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<PortalInvoiceListResponse> response = listPortalInvoicesUseCase.executePage(
                dateFrom, dateTo, statuses, returnStatus, productId, categoryId,
                page, size, search, orderBy, orderDir);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceDetailResponse> getInvoice(@PathVariable UUID id) {
        InvoiceDetailResponse response = getPortalInvoiceUseCase.execute(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    public ResponseEntity<PortalInvoiceSummaryResponse> getSummary() {
        List<PortalInvoiceListResponse> invoices = listPortalInvoicesUseCase.execute(
                null, null, null, null, null, null, null, null);

        BigDecimal totalDebt = BigDecimal.ZERO;
        long pendingCount = 0;
        BigDecimal totalPendingApproval = BigDecimal.ZERO;

        List<String> debtStatuses = List.of("PENDING_PAYMENT", "PARTIALLY_PAID", "OVERDUE");
        for (PortalInvoiceListResponse inv : invoices) {
            if (debtStatuses.contains(inv.status())) {
                totalDebt = totalDebt.add(inv.total().subtract(inv.paidAmount()));
                pendingCount++;
            }
            totalPendingApproval = totalPendingApproval.add(inv.pendingApprovalAmount());
        }

        BigDecimal paidThisMonth = BigDecimal.ZERO;
        return ResponseEntity.ok(new PortalInvoiceSummaryResponse(totalDebt, pendingCount, paidThisMonth, totalPendingApproval));
    }
}
