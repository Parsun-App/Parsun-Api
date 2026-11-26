package com.parsun.parsun_api.platform.auth.user.application.update.use_case;

import com.parsun.parsun_api.platform.auth.preferences.domain.repository.UserPreferencesRepository;

import com.parsun.parsun_api.platform.auth.authorization.application.services.AccessService;
import com.parsun.parsun_api.platform.auth.user.application.dto.response.UserResponse;
import com.parsun.parsun_api.platform.auth.user.application.exceptions.UserNotFoundException;
import com.parsun.parsun_api.platform.auth.user.application.update.request.UpdateOwnProfileRequest;
import com.parsun.parsun_api.platform.auth.user.domain.repository.UserRepository;
import com.parsun.parsun_api.platform.auth.user.domain.valueobjects.UserId;
import com.parsun.parsun_api.platform.auth.user.domain.valueobjects.UserName;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

@Service
public class UpdateOwnProfileUseCase {
    private final UserPreferencesRepository preferences;

    private final UserRepository users;
    private final AccessService access;

    public UpdateOwnProfileUseCase(UserRepository users, AccessService access,
            UserPreferencesRepository preferences) {
        this.preferences = preferences;
        this.users = users;
        this.access = access;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Audited(module = "platform", entityType = "USER", action = "EDIT")
    public UserResponse execute(String authenticatedUserId,
            UpdateOwnProfileRequest request) {
        // Serialize with administrative writes before loading the account so assignments cannot be overwritten.
        access.lockAdministration();
        var user = users.findById(UserId.from(authenticatedUserId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.updateUserName(new UserName(request.userName()));
        user.updatePersonalProfile(request.profile());
        var saved = users.save(user);
        if (request.language() != null)
            preferences.updateLanguage(saved.getId().getValue(),
                    request.language());
        return UserResponse.from(saved).withLanguage(
                preferences.languageFor(saved.getId().getValue()));
    }
}
