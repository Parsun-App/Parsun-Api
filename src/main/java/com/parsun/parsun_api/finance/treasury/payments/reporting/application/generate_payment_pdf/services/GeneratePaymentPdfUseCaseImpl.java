package com.parsun.parsun_api.finance.treasury.payments.reporting.application.generate_payment_pdf.services;

import com.parsun.parsun_api.finance.treasury.payments.reporting.application.generate_payment_pdf.use_case.GeneratePaymentPdfUseCase;

import com.parsun.parsun_api.finance.treasury.payments.reporting.application.dto.model.PaymentReportData;
import com.parsun.parsun_api.finance.treasury.payments.reporting.application.ports.out.PaymentReportQueryGateway;
import com.parsun.parsun_api.finance.treasury.payments.reporting.application.ports.out.PaymentPdfPort;

import java.util.UUID;

public class GeneratePaymentPdfUseCaseImpl
        implements
            GeneratePaymentPdfUseCase {

    private final PaymentReportQueryGateway paymentQuery;
    private final PaymentPdfPort paymentPdfPort;

    public GeneratePaymentPdfUseCaseImpl(PaymentReportQueryGateway paymentQuery,
            PaymentPdfPort paymentPdfPort) {
        this.paymentQuery = paymentQuery;
        this.paymentPdfPort = paymentPdfPort;
    }

    @Override
    public byte[] execute(UUID paymentId) {
        PaymentReportData payment = paymentQuery.find(paymentId);
        return paymentPdfPort.generatePaymentPdf(payment);
    }
}
