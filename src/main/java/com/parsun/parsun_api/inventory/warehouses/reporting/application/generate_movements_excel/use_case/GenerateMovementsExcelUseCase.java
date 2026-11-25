package com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_excel.use_case;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.MovementsPdfQuery;

public interface GenerateMovementsExcelUseCase {
    byte[] execute(MovementsPdfQuery query);
}
