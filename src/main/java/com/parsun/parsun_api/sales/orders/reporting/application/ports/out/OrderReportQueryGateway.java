package com.parsun.parsun_api.sales.orders.reporting.application.ports.out;

import com.parsun.parsun_api.sales.orders.reporting.application.dto.model.OrderReportData;
import com.parsun.parsun_api.sales.orders.reporting.application.dto.model.OrderReportRow;
import com.parsun.parsun_api.sales.orders.reporting.application.dto.model.OrderReportFilter;
import java.util.List;
import java.util.UUID;

public interface OrderReportQueryGateway {
    OrderReportData find(UUID id);
    List<OrderReportRow> findAll(OrderReportFilter filter);
}
