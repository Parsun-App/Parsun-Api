package com.cashly.cashly_api.portal.infrastructure.web;

import com.cashly.cashly_api.portal.application.dto.response.PortalDashboardResponse;
import com.cashly.cashly_api.portal.application.operations.use_case.GetPortalDashboardUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('ROLE_PORTAL_CUSTOMER')")
@RequestMapping("/api/portal/dashboard")
public class PortalDashboardController {

    private final GetPortalDashboardUseCase getPortalDashboardUseCase;

    public PortalDashboardController(GetPortalDashboardUseCase getPortalDashboardUseCase) {
        this.getPortalDashboardUseCase = getPortalDashboardUseCase;
    }

    @GetMapping
    public ResponseEntity<PortalDashboardResponse> getDashboard() {
        PortalDashboardResponse response = getPortalDashboardUseCase.execute();
        return ResponseEntity.ok(response);
    }
}
