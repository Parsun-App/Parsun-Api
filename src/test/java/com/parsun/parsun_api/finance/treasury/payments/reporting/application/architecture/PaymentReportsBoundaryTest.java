package com.parsun.parsun_api.finance.treasury.payments.reporting.application.architecture;
import com.parsun.parsun_api.finance.treasury.payments.reporting.application.ports.out.PaymentPdfPort;
import com.parsun.parsun_api.finance.treasury.payments.reporting.application.generate_payment_pdf.services.GeneratePaymentPdfUseCaseImpl;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertFalse;
class PaymentReportsBoundaryTest {
    @Test
    void applicationUsesOwnContractsAndDtos() {
        for (var type : new Class<?>[]{PaymentPdfPort.class,
                GeneratePaymentPdfUseCaseImpl.class}) {
            Arrays.stream(type.getDeclaredFields()).forEach(
                    field -> assertFalse(field.getType().getName().matches(
                            ".*parsun_api.finance.treasury.payments.(?!reporting\\.).*"),
                            field.toString()));
            Arrays.stream(type.getDeclaredMethods()).forEach(method -> Arrays
                    .stream(method.getGenericParameterTypes())
                    .forEach(parameter -> assertFalse(
                            parameter.getTypeName().matches(
                                    ".*parsun_api.finance.treasury.payments.(?!reporting\\.).*"),
                            parameter.toString())));
        }
    }
}
