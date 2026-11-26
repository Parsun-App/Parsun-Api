package com.parsun.parsun_api.finance.treasury.receivables.reporting.application.ports.out;

import com.parsun.parsun_api.finance.treasury.receivables.reporting.application.dto.model.CollectionsReportData;

public interface CollectionsReportPdfPort {
    byte[] generateReport(CollectionsReportData data);
}
