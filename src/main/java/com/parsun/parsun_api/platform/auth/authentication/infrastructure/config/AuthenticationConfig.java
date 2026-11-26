package com.parsun.parsun_api.platform.auth.authentication.infrastructure.config;

import com.parsun.parsun_api.platform.auth.authentication.application.ports.out.PortalCustomerQuery;
import com.parsun.parsun_api.platform.auth.authentication.infrastructure.integration.CustomerRifResolver;
import com.parsun.parsun_api.platform.auth.token.application.ports.out.AuthenticableSubjectGateway;
import com.parsun.parsun_api.platform.auth.token.infrastructure.integration.LocalAuthenticableSubjectGateway;
import com.parsun.parsun_api.platform.auth.authentication.domain.services.AuthenticationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.parsun.parsun_api.platform.auth.authentication.application.login.use_case.LoginUserUseCase;
import com.parsun.parsun_api.platform.auth.authentication.domain.services.AuthenticableResolver;
import com.parsun.parsun_api.platform.auth.authentication.infrastructure.services.ChainedAuthenticableResolver;
import com.parsun.parsun_api.platform.auth.token.application.refresh.use_case.RefreshTokenUseCase;
import com.parsun.parsun_api.platform.auth.token.domain.repository.RefreshTokenRepository;
import com.parsun.parsun_api.platform.auth.token.domain.services.TokenService;
import com.parsun.parsun_api.platform.auth.user.domain.repository.UserRepository;
import com.parsun.parsun_api.platform.auth.user.domain.services.PasswordEncoder;

import com.parsun.parsun_api.platform.auth.mfa.application.services.MfaService;
import com.parsun.parsun_api.platform.auth.mfa.application.ports.out.MfaStore;
@Configuration
public class AuthenticationConfig {

    @Bean
    public AuthenticableResolver authenticableResolver(
            UserRepository userRepository,
            com.parsun.parsun_api.platform.auth.authorization.application.services.AccessService access) {
        return new ChainedAuthenticableResolver(userRepository, access);
    }

    @Bean
    public LoginUserUseCase loginUserUseCase(
            AuthenticableResolver authenticableResolver,
            RefreshTokenRepository refreshTokenRepository,
            TokenService tokenService, PasswordEncoder passwordEncoder,
            com.parsun.parsun_api.platform.auth.mfa.application.services.MfaService mfa) {
        return new LoginUserUseCase(authenticableResolver,
                refreshTokenRepository, tokenService, passwordEncoder, mfa);
    }

    @Bean
    public AuthenticationDomainService authenticationDomainService() {
        return new AuthenticationDomainService();
    }

    @Bean
    public CustomerRifResolver customerRifResolver(
            PortalCustomerQuery portalCustomers) {
        return new CustomerRifResolver(portalCustomers);
    }

    @Bean
    public AuthenticableSubjectGateway authenticableSubjectGateway(
            UserRepository userRepository,
            PortalCustomerQuery portalCustomers,
            com.parsun.parsun_api.platform.auth.authorization.application.services.AccessService access) {
        return new LocalAuthenticableSubjectGateway(userRepository,
                portalCustomers, access);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(
            RefreshTokenRepository refreshTokenRepository,
            AuthenticableSubjectGateway subjectGateway,
            TokenService tokenService,
            com.parsun.parsun_api.platform.auth.mfa.application.ports.out.MfaStore mfaStore) {
        return new RefreshTokenUseCase(refreshTokenRepository, subjectGateway,
                tokenService, mfaStore);
    }
}
