package com.parsun.parsun_api.platform.audit.application.ports.in;

import com.parsun.parsun_api.platform.audit.application.record.model.AuditEntry;
import com.parsun.parsun_api.platform.audit.domain.valueobjects.AuditAction;
import com.parsun.parsun_api.platform.audit.domain.valueobjects.AuditEntityType;

import java.util.UUID;

public interface AuditRecorder {

    AuditRecorder NONE = entry -> {
    };

    void record(AuditEntry entry);

    default void record(String module, String entityType, UUID entityId,
            String entityLabel, String action) {
        record(AuditEntry.of(module, AuditEntityType.valueOf(entityType),
                entityId, entityLabel, AuditAction.valueOf(action)));
    }
}
