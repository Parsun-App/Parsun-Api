package com.parsun.parsun_api.platform.auth.token.application.ports.out;

import com.parsun.parsun_api.platform.auth.authentication.domain.entity.Authenticable;
import com.parsun.parsun_api.platform.auth.authentication.domain.valueobjects.AuthenticableType;

public interface AuthenticableSubjectGateway {
    Authenticable resolve(String subjectId, AuthenticableType subjectType);
}
