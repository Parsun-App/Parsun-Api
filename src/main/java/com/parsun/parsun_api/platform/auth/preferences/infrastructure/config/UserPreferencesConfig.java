package com.parsun.parsun_api.platform.auth.preferences.infrastructure.config;

import com.parsun.parsun_api.platform.auth.authorization.application.ports.in.CurrentActor;
import com.parsun.parsun_api.platform.auth.preferences.application.services.UserPreferencesService;
import com.parsun.parsun_api.platform.auth.preferences.domain.repository.UserPreferencesRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserPreferencesConfig {
    @Bean
    UserPreferencesService userPreferencesService(
            UserPreferencesRepository repository, CurrentActor actor) {
        return new UserPreferencesService(repository, actor);
    }
}
