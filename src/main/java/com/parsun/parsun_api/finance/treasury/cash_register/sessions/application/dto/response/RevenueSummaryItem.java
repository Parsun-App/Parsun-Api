package com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.dto.response;

import java.math.BigDecimal;

public record RevenueSummaryItem(String paymentMethod, BigDecimal total) {
}
