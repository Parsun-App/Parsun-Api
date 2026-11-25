package com.parsun.parsun_api.infrastructure.seed.steps;

import static com.parsun.parsun_api.infrastructure.seed.SeedIds.ADMIN_USER;
import static com.parsun.parsun_api.infrastructure.seed.SeedIds.ADMIN_USER_ALT;
import static com.parsun.parsun_api.infrastructure.seed.SeedIds.OPERATOR_USER;

import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.platform.auth.mfa.infrastructure.persistence.adapters.JdbcMfaStore;
import org.springframework.stereotype.Component;

@Component
public class V28MfaSeed implements SeedStep {
    private final JdbcMfaStore mfaStore;
    public V28MfaSeed(JdbcMfaStore mfaStore) {
        this.mfaStore = mfaStore;
    }
    @Override
    public int version() {
        return 28;
    }
    @Override
    public void seed() {
        mfaStore.lock(ADMIN_USER.toString());
        mfaStore.lock(ADMIN_USER_ALT.toString());
        mfaStore.lock(OPERATOR_USER.toString());
    }
}
