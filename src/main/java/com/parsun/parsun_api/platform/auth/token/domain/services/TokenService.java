package com.parsun.parsun_api.platform.auth.token.domain.services;

import com.parsun.parsun_api.platform.auth.authentication.domain.entity.Authenticable;
import com.parsun.parsun_api.platform.auth.authentication.domain.valueobjects.AuthenticableType;
import com.parsun.parsun_api.platform.auth.token.domain.entity.RefreshToken;
import com.parsun.parsun_api.platform.auth.user.domain.entity.User;
import com.parsun.parsun_api.platform.auth.user.domain.valueobjects.Email;
import com.parsun.parsun_api.platform.auth.user.domain.valueobjects.UserId;

import java.time.LocalDateTime;

public interface TokenService {

    String generateAccessToken(User user);

    String generateAccessToken(Authenticable authenticable);

    RefreshToken generateRefreshToken(User user);

    RefreshToken generateRefreshToken(Authenticable authenticable);

    boolean validateToken(String token);

    UserId extractUserId(String token);

    String extractSubjectId(String token);

    AuthenticableType extractSubjectType(String token);

    Email extractEmail(String token);

    LocalDateTime getExpirationDate(String token);

    long getAccessTokenExpiration();

    long getRefreshTokenExpiration();
}
