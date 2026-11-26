package com.parsun.parsun_api.platform.auth.authentication.domain.services;

import java.util.Optional;

import com.parsun.parsun_api.platform.auth.authentication.domain.entity.Authenticable;

public interface AuthenticableResolver {

    Optional<Authenticable> resolveByEmail(String email);

    default Optional<Authenticable> resolveByRif(String rif) {
        return Optional.empty();
    }
}
