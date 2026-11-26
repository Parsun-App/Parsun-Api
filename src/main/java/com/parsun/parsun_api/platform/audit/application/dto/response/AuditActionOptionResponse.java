package com.parsun.parsun_api.platform.audit.application.dto.response;

public record AuditActionOptionResponse(
        String value,
        String labelKey,
        long count) {
}
