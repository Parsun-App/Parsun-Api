package com.parsun.parsun_api.platform.auth.authentication.application.login.use_case;

import com.parsun.parsun_api.platform.auth.authentication.application.dto.response.AuthenticatedEntityResponse;
import com.parsun.parsun_api.platform.auth.authentication.application.dto.response.AuthenticationResponse;
import com.parsun.parsun_api.platform.auth.authentication.application.login.request.LoginRequest;
import com.parsun.parsun_api.platform.auth.authentication.domain.entity.Authenticable;
import com.parsun.parsun_api.platform.auth.authentication.domain.services.AuthenticableResolver;
import com.parsun.parsun_api.platform.auth.user.domain.services.PasswordEncoder;
import com.parsun.parsun_api.platform.auth.token.domain.repository.RefreshTokenRepository;
import com.parsun.parsun_api.platform.auth.token.domain.services.TokenService;
import com.parsun.parsun_api.platform.auth.token.domain.entity.RefreshToken;
import com.parsun.parsun_api.platform.auth.authentication.application.exceptions.InvalidCredentialsException;
import org.springframework.transaction.annotation.Transactional;

import com.parsun.parsun_api.platform.auth.mfa.application.services.MfaService;
public class LoginUserUseCase {

    private final com.parsun.parsun_api.platform.auth.mfa.application.services.MfaService mfa;

    private final AuthenticableResolver authenticableResolver;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public LoginUserUseCase(AuthenticableResolver authenticableResolver,
            RefreshTokenRepository refreshTokenRepository,
            TokenService tokenService, PasswordEncoder passwordEncoder,
            com.parsun.parsun_api.platform.auth.mfa.application.services.MfaService mfa) {
        this.mfa = mfa;
        this.authenticableResolver = authenticableResolver;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public AuthenticationResponse execute(LoginRequest request) {
        String email = normalizeEmail(request.email());

        Authenticable authenticable = authenticableResolver
                .resolveByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Invalid email or password"));

        if (!authenticable.canAuthenticate()) {
            throw new InvalidCredentialsException("Account is not active");
        }

        if (!passwordEncoder.matches(request.password(),
                authenticable.getHashedPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        var challenge = mfa.challengeIfEnabled(authenticable);
        if (challenge != null)
            return challenge;

        String accessToken = tokenService.generateAccessToken(authenticable);

        RefreshToken refreshToken = tokenService
                .generateRefreshToken(authenticable);
        refreshTokenRepository.save(refreshToken);

        AuthenticatedEntityResponse entityResponse = AuthenticatedEntityResponse
                .from(authenticable);
        return AuthenticationResponse.of(accessToken, refreshToken.getToken(),
                tokenService.getAccessTokenExpiration(), entityResponse);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase();
    }
}
