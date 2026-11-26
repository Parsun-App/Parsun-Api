package com.parsun.parsun_api.platform.audit.application.mappers;

import com.parsun.parsun_api.platform.audit.application.dto.response.AuditActionOptionResponse;
import com.parsun.parsun_api.platform.audit.application.dto.response.AuditActorOptionResponse;
import com.parsun.parsun_api.platform.audit.application.dto.response.AuditActorResponse;
import com.parsun.parsun_api.platform.audit.application.dto.response.AuditDateRangeResponse;
import com.parsun.parsun_api.platform.audit.application.dto.response.AuditEventResponse;
import com.parsun.parsun_api.platform.audit.application.dto.response.AuditFilterOptionsResponse;
import com.parsun.parsun_api.platform.audit.application.ports.out.AuditQueryPort;
import com.parsun.parsun_api.platform.audit.domain.entity.AuditEvent;

import java.util.List;

public class AuditMapper {

    public AuditEventResponse toResponse(AuditEvent event) {
        return new AuditEventResponse(event.id(), event.module(),
                event.entityType().name(), event.entityId(), event.entityLabel(),
                event.entitySecondaryLabel(), event.action().name(),
                new AuditActorResponse(event.actorId(), event.actorName(),
                        event.actorPhotoUrl()),
                event.occurredAt(), event.descriptionKey(),
                event.descriptionArgs(), event.metadata());
    }

    public AuditFilterOptionsResponse toResponse(
            AuditQueryPort.AuditFilterOptions options) {
        List<AuditActionOptionResponse> actions = options.actions().stream()
                .map(action -> new AuditActionOptionResponse(
                        action.action().name(), action.action().labelKey(),
                        action.count()))
                .toList();
        List<AuditActorOptionResponse> actors = options.actors().stream()
                .map(actor -> new AuditActorOptionResponse(actor.actorId(),
                        actor.actorName(), actor.photoUrl(), actor.count()))
                .toList();
        AuditQueryPort.DateRange range = options.dateRange();
        return new AuditFilterOptionsResponse(actions, actors,
                new AuditDateRangeResponse(range == null ? null : range.min(),
                        range == null ? null : range.max()));
    }
}
