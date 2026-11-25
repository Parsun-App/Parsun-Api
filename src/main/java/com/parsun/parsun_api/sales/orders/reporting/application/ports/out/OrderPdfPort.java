package com.parsun.parsun_api.sales.orders.reporting.application.ports.out;

import com.parsun.parsun_api.sales.orders.reporting.application.dto.model.OrderReportData;

public interface OrderPdfPort {
    byte[] generatePdf(OrderReportData order);
}