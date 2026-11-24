package com.cashly.cashly_api.finance.treasury.cash_register.sessions.application.open.request;

import jakarta.validation.constraints.NotBlank;

public record OpenRegisterRequest(@NotBlank String openedByName) {
}
