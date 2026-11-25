package com.parsun.parsun_api.portal.infrastructure.web;

import com.parsun.parsun_api.platform.auth.authentication.application.dto.response.AuthenticationResponse;
import com.parsun.parsun_api.platform.auth.token.application.logout.request.LogoutRequest;
import com.parsun.parsun_api.platform.auth.token.application.refresh.request.RefreshTokenRequest;
import com.parsun.parsun_api.platform.auth.token.application.logout.use_case.LogoutUserUseCase;
import com.parsun.parsun_api.platform.auth.token.application.refresh.use_case.RefreshTokenUseCase;
import com.parsun.parsun_api.portal.application.dto.request.ChangeCustomerPasswordRequest;
import com.parsun.parsun_api.portal.application.dto.request.PortalLoginRequest;
import com.parsun.parsun_api.portal.application.dto.response.CustomerSummaryResponse;
import com.parsun.parsun_api.portal.application.dto.response.PortalAuthResponse;
import com.parsun.parsun_api.portal.application.operations.use_case.ChangeCustomerPasswordUseCase;
import com.parsun.parsun_api.portal.application.operations.use_case.PortalLoginUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portal/auth")
public class PortalAuthController {

    private final PortalLoginUseCase portalLoginUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final ChangeCustomerPasswordUseCase changeCustomerPasswordUseCase;
    private final LogoutUserUseCase logoutUserUseCase;

    public PortalAuthController(PortalLoginUseCase portalLoginUseCase,
                                 RefreshTokenUseCase refreshTokenUseCase,
                                 ChangeCustomerPasswordUseCase changeCustomerPasswordUseCase,
                                 LogoutUserUseCase logoutUserUseCase) {
        this.portalLoginUseCase = portalLoginUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.changeCustomerPasswordUseCase = changeCustomerPasswordUseCase;
        this.logoutUserUseCase = logoutUserUseCase;
    }

    @PostMapping("/login")
    @PreAuthorize("permitAll()")
    public ResponseEntity<PortalAuthResponse> login(@Valid @RequestBody PortalLoginRequest request) {
        PortalAuthResponse response = portalLoginUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @PreAuthorize("permitAll()")
    public ResponseEntity<AuthenticationResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthenticationResponse response = refreshTokenUseCase.execute(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    @PreAuthorize("hasAuthority('ROLE_PORTAL_CUSTOMER')")
    public ResponseEntity<CustomerSummaryResponse> changePassword(
            Authentication authentication,
            @Valid @RequestBody ChangeCustomerPasswordRequest request) {
        String customerId = (String) authentication.getPrincipal();
        CustomerSummaryResponse response = changeCustomerPasswordUseCase.execute(customerId, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @PreAuthorize("hasAuthority('ROLE_PORTAL_CUSTOMER')")
    public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {
        logoutUserUseCase.execute(request);
        return ResponseEntity.noContent().build();
    }
}
