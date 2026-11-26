package com.parsun.parsun_api.platform.auth.mfa.application.ports.out;

import com.parsun.parsun_api.platform.auth.mfa.domain.entity.MfaAccount;
public interface MfaStore {
    MfaAccount lock(String subjectId);
    MfaAccount find(String subjectId);
    MfaAccount lockByChallenge(String hash);
    void save(MfaAccount account);
}
