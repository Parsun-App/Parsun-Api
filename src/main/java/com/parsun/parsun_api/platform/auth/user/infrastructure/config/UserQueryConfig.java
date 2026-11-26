package com.parsun.parsun_api.platform.auth.user.infrastructure.config;

import com.parsun.parsun_api.platform.auth.user.application.ports.in.NotificationRecipientsQuery;
import com.parsun.parsun_api.platform.auth.user.application.queries.services.RepositoryNotificationRecipientsQuery;
import com.parsun.parsun_api.platform.auth.user.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserQueryConfig {
    @Bean
    public NotificationRecipientsQuery notificationRecipientsQuery(
            UserRepository userRepository) {
        return new RepositoryNotificationRecipientsQuery(userRepository);
    }
}
