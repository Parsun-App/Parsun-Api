package com.parsun.parsun_api.portal.application.dto.response;

import java.math.BigDecimal;

public record PortalPaymentSummaryResponse(BigDecimal appliedTotal,
        BigDecimal pendingTotal, long totalPayments) {
}
