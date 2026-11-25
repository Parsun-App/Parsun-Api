package com.parsun.parsun_api.catalog.categories.application.update.use_case;

import com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission;
import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;

import com.parsun.parsun_api.catalog.categories.application.update.request.UpdateCategoryRequest;
import com.parsun.parsun_api.catalog.categories.application.dto.response.CategoryDetailResponse;
import com.parsun.parsun_api.catalog.categories.application.exceptions.CategoryNotFoundException;
import com.parsun.parsun_api.catalog.categories.application.exceptions.DuplicateCategoryNameException;
import com.parsun.parsun_api.catalog.categories.application.mappers.CategoryMapper;
import com.parsun.parsun_api.catalog.categories.domain.entity.Category;
import com.parsun.parsun_api.catalog.categories.domain.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class UpdateCategoryUseCase {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ActiveTogglePermission access;

    private final ActivityNotificationRecorder activityNotifications;

    public UpdateCategoryUseCase(CategoryRepository categoryRepository,
            CategoryMapper categoryMapper, ActiveTogglePermission access,
            ActivityNotificationRecorder activityNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.access = access;
    }

    @Transactional
    @Audited(module = "catalog", entityType = "CATEGORY", action = "EDIT")
    public CategoryDetailResponse execute(UUID id,
            UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (categoryRepository.existsByNameAndIdNot(request.name().trim(),
                id)) {
            throw new DuplicateCategoryNameException(request.name());
        }

        if (request.active() != null
                && request.active() != category.isActive()) {
            access.requireActiveToggle();
        }

        category.update(request.name().trim(), request.description(),
                request.active());
        Category saved = categoryRepository.save(category);
        activityNotifications.record(ActivityEvent.of("CATALOG_CATEGORIES",
                "EDIT", "CATEGORY", saved.getId(),
                saved.getName(), null));
        return categoryMapper.toDetailResponse(saved, 0L);
    }
}
