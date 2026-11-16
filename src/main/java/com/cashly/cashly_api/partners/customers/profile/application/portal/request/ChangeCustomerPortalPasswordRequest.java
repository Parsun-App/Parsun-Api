package com.cashly.cashly_api.partners.customers.profile.application.portal.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeCustomerPortalPasswordRequest(
        @NotBlank(message = "New password is required") @Size(min = 6, max = 100, message = "Password must contain between 6 and 100 characters") String newPassword) {
}
