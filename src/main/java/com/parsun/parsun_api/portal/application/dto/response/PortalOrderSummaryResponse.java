package com.parsun.parsun_api.portal.application.dto.response;

import java.math.BigDecimal;

public record PortalOrderSummaryResponse(long pendingCount, BigDecimal totalAmount) {
}
