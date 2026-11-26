package com.parsun.parsun_api.finance.treasury.payments.collection.application.reject.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RejectPaymentRequest(

        @NotNull @NotBlank String reason

) {
}
