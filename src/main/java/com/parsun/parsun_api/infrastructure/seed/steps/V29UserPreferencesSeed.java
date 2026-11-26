package com.parsun.parsun_api.infrastructure.seed.steps;

import static com.parsun.parsun_api.infrastructure.seed.SeedIds.ADMIN_USER;
import static com.parsun.parsun_api.infrastructure.seed.SeedIds.ADMIN_USER_ALT;
import static com.parsun.parsun_api.infrastructure.seed.SeedIds.OPERATOR_USER;

import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import com.parsun.parsun_api.platform.auth.preferences.domain.entity.UserPreferences;
import com.parsun.parsun_api.platform.auth.preferences.domain.repository.UserPreferencesRepository;
import org.springframework.stereotype.Component;

@Component
public class V29UserPreferencesSeed implements SeedStep {
    private final UserPreferencesRepository repository;
    public V29UserPreferencesSeed(UserPreferencesRepository repository) {
        this.repository = repository;
    }
    @Override
    public int version() {
        return 29;
    }
    @Override
    public void seed() {
        repository.save(ADMIN_USER, new UserPreferences("dark", "minimal",
                false, false, "es"));
        repository.save(ADMIN_USER_ALT, new UserPreferences("dark", "minimal",
                false, false, "es"));
        repository.save(OPERATOR_USER, new UserPreferences("system", "minimal",
                true, false, "es"));
    }
}
