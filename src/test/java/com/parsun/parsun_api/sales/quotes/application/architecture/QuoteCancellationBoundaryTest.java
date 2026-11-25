package com.parsun.parsun_api.sales.quotes.application.architecture;

import com.parsun.parsun_api.sales.quotes.application.cancel.use_case.CancelQuoteUseCase;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuoteCancellationBoundaryTest {
    @Test
    void should_depend_on_an_owned_notification_contract() {
        assertTrue(Arrays
                .stream(CancelQuoteUseCase.class.getDeclaredConstructors())
                .flatMap(constructor -> Arrays
                        .stream(constructor.getParameterTypes()))
                .filter(type -> type.getPackageName().startsWith(
                        "com.parsun.parsun_api.platform.notifications"))
                .allMatch(type -> type.getPackageName().equals(
                        "com.parsun.parsun_api.platform.notifications.application.ports.in")));
    }
}
