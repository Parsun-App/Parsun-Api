package com.parsun.parsun_api.sales.invoices.returns.application.architecture;

import com.parsun.parsun_api.sales.invoices.returns.application.create.use_case.CreateReturnUseCase;
import com.parsun.parsun_api.sales.invoices.returns.application.delete.use_case.DeleteReturnUseCase;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ReturnInventoryBoundaryTest {
    @Test
    void shouldRejectWarehouseDependenciesInApplicationConstructors() {
        for (Class<?> useCase : List.of(CreateReturnUseCase.class,
                DeleteReturnUseCase.class)) {
            for (var constructor : useCase.getConstructors()) {
                for (Class<?> parameter : constructor.getParameterTypes()) {
                    assertFalse(parameter.getPackageName().startsWith(
                            "com.parsun.parsun_api.inventory.warehouses"),
                            parameter.getName());
                }
            }
        }
    }
}
