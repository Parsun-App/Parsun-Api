package com.parsun.parsun_api.platform.audit.domain.repository;

import com.parsun.parsun_api.platform.audit.domain.entity.AuditEvent;

public interface AuditEventRepository {
    AuditEvent save(AuditEvent event);
}
