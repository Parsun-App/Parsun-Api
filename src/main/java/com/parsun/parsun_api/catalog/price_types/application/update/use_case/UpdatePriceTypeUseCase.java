package com.parsun.parsun_api.catalog.price_types.application.update.use_case;

import com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission;
import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityEvent;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;

import com.parsun.parsun_api.catalog.price_types.application.update.request.UpdatePriceTypeRequest;
import com.parsun.parsun_api.catalog.price_types.application.dto.response.PriceTypeResponse;
import com.parsun.parsun_api.catalog.price_types.application.exceptions.DuplicatePriceTypeNameException;
import com.parsun.parsun_api.catalog.price_types.application.exceptions.PriceTypeNotFoundException;
import com.parsun.parsun_api.catalog.price_types.domain.entity.PriceType;
import com.parsun.parsun_api.catalog.price_types.domain.repository.PriceTypeRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import com.parsun.parsun_api.platform.audit.application.ports.in.Audited;

public class UpdatePriceTypeUseCase {

    private final PriceTypeRepository priceTypeRepository;
    private final ActiveTogglePermission access;

    private final ActivityNotificationRecorder activityNotifications;

    public UpdatePriceTypeUseCase(PriceTypeRepository priceTypeRepository,
            ActiveTogglePermission access,
            ActivityNotificationRecorder activityNotifications) {
        this.activityNotifications = activityNotifications != null
                ? activityNotifications
                : ActivityNotificationRecorder.NONE;
        this.priceTypeRepository = priceTypeRepository;
        this.access = access;
    }

    @Transactional
    @Audited(module = "catalog", entityType = "PRICE_TYPE", action = "EDIT")
    public PriceTypeResponse execute(UUID id, UpdatePriceTypeRequest request) {
        PriceType priceType = priceTypeRepository.findById(id)
                .orElseThrow(() -> new PriceTypeNotFoundException(id));

        if (priceTypeRepository.existsByNameAndIdNot(request.name(), id)) {
            throw new DuplicatePriceTypeNameException(request.name());
        }

        if (request.active() != null
                && request.active() != priceType.isActive()) {
            access.requireActiveToggle();
        }

        priceType.update(request.name(), request.description(),
                request.active());
        PriceType saved = priceTypeRepository.save(priceType);
        activityNotifications.record(ActivityEvent.of("CATALOG_PRICES",
                "EDIT", "PRICE_TYPE", saved.getId(),
                saved.getName(), null));
        return PriceTypeResponse.from(saved);
    }
}
