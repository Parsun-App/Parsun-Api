package com.parsun.parsun_api.inventory.warehouses.reporting.application.generate_movements_pdf.use_case;

import com.parsun.parsun_api.inventory.warehouses.reporting.application.dto.query.model.MovementsPdfQuery;

public interface GenerateMovementsPdfUseCase {
    byte[] execute(MovementsPdfQuery query);
}
