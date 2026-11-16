package com.cashly.cashly_api.platform.auth.token.infrastructure.integration;

import com.cashly.cashly_api.platform.auth.authentication.application.ports.out.PortalCustomerQuery;
import com.cashly.cashly_api.platform.auth.authentication.domain.entity.Authenticable;
import com.cashly.cashly_api.platform.auth.authentication.domain.valueobjects.AuthenticableType;
import com.cashly.cashly_api.platform.auth.authentication.infrastructure.integration.CustomerAuthenticableAdapter;
import com.cashly.cashly_api.platform.auth.authentication.infrastructure.integration.UserAuthenticableAdapter;
import com.cashly.cashly_api.platform.auth.token.application.exceptions.InvalidTokenException;
import com.cashly.cashly_api.platform.auth.token.application.ports.out.AuthenticableSubjectGateway;
import com.cashly.cashly_api.platform.auth.user.application.exceptions.UserNotFoundException;
import com.cashly.cashly_api.platform.auth.user.domain.repository.UserRepository;
import com.cashly.cashly_api.platform.auth.user.domain.valueobjects.UserId;

import java.util.UUID;

public class LocalAuthenticableSubjectGateway
        implements
            AuthenticableSubjectGateway {
    private final UserRepository userRepository;
    private final PortalCustomerQuery portalCustomers;
    private final com.cashly.cashly_api.platform.auth.authorization.application.services.AccessService access;

    public LocalAuthenticableSubjectGateway(UserRepository userRepository,
            PortalCustomerQuery portalCustomers,
            com.cashly.cashly_api.platform.auth.authorization.application.services.AccessService access) {
        this.userRepository = userRepository;
        this.portalCustomers = portalCustomers;
        this.access = access;
    }

    public LocalAuthenticableSubjectGateway(UserRepository userRepository,
            com.cashly.cashly_api.platform.auth.authorization.application.services.AccessService access) {
        this(userRepository, null, access);
    }

    @Override
    public Authenticable resolve(String subjectId,
            AuthenticableType subjectType) {
        return switch (subjectType) {
            case USER -> {
                access.resolve(UUID.fromString(subjectId));
                yield new UserAuthenticableAdapter(
                        userRepository.findById(UserId.from(subjectId))
                                .orElseThrow(() -> new UserNotFoundException(
                                        "User not found")));
            }
            case CUSTOMER -> {
                if (portalCustomers == null) {
                    throw new InvalidTokenException("Customer not found");
                }
                yield portalCustomers.findById(UUID.fromString(subjectId))
                        .map(CustomerAuthenticableAdapter::new)
                        .orElseThrow(() -> new InvalidTokenException(
                                "Customer not found"));
            }
        };
    }
}
