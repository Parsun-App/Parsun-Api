package com.parsun.parsun_api.platform.auth.user.infrastructure.config;
import com.parsun.parsun_api.platform.auth.user.application.ports.in.ActorIdentityQuery;
import com.parsun.parsun_api.platform.auth.user.application.queries.services.RepositoryActorIdentityQuery;
import com.parsun.parsun_api.platform.auth.user.domain.repository.UserRepository;
import com.parsun.parsun_api.platform.auth.user.domain.services.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ActorIdentityQueryConfig {
    @Bean
    public ActorIdentityQuery actorIdentityQuery(UserRepository users,
            PasswordEncoder passwords) {
        return new RepositoryActorIdentityQuery(users, passwords);
    }
}
