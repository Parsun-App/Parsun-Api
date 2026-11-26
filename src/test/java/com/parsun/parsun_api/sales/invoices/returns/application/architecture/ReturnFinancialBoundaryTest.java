package com.parsun.parsun_api.sales.invoices.returns.application.architecture;

import com.parsun.parsun_api.sales.invoices.returns.application.create.use_case.CreateReturnUseCase;
import com.parsun.parsun_api.sales.invoices.returns.application.delete.use_case.DeleteReturnUseCase;
import com.parsun.parsun_api.sales.invoices.returns.application.services.ReturnResponseAssembler;
import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
class ReturnFinancialBoundaryTest {
    @ParameterizedTest
    @ValueSource(classes = {CreateReturnUseCase.class,
            DeleteReturnUseCase.class, ReturnResponseAssembler.class})
    void should_depend_only_on_return_owned_contracts(Class<?> component) {
        var dependencies = Arrays
                .stream(component.getConstructors()[0].getParameterTypes())
                .map(Class::getName).toList();
        assertTrue(
                dependencies.stream().allMatch(type -> type.startsWith(
                        "com.parsun.parsun_api.sales.invoices.returns.")
                        || type.startsWith(
                                "com.parsun.parsun_api.platform.notifications.application.ports.in.")),
                component.getSimpleName() + dependencies);
    }
}
