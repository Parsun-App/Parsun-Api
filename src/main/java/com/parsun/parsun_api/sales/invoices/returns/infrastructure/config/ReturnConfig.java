package com.parsun.parsun_api.sales.invoices.returns.infrastructure.config;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnBalanceGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnCashGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnInventoryGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnInvoiceAdjustmentGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnInvoiceQueryGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.ports.out.ReturnPaymentSettlementGateway;
import com.parsun.parsun_api.sales.invoices.returns.application.services.ReturnResponseAssembler;
import com.parsun.parsun_api.sales.invoices.returns.application.create.use_case.CreateReturnUseCase;
import com.parsun.parsun_api.sales.invoices.returns.application.delete.use_case.DeleteReturnUseCase;
import com.parsun.parsun_api.sales.invoices.returns.application.list.use_case.GetAllReturnsUseCase;
import com.parsun.parsun_api.sales.invoices.returns.application.get.use_case.GetReturnByIdUseCase;
import com.parsun.parsun_api.sales.invoices.returns.domain.repository.ReturnRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ReturnConfig {
    private final ReturnRepository returnRepository;
    public ReturnConfig(ReturnRepository returnRepository) {
        this.returnRepository = returnRepository;
    }
    @Bean
    public CreateReturnUseCase createReturnUseCase(
            ReturnInvoiceAdjustmentGateway adjustments,
            ReturnResponseAssembler responses,
            ReturnInvoiceQueryGateway invoices,
            ReturnPaymentSettlementGateway payments,
            ReturnBalanceGateway balances, ReturnInventoryGateway inventory,
            ReturnCashGateway cash,
            ActivityNotificationRecorder activityNotifications) {
        return new CreateReturnUseCase(adjustments, returnRepository, responses,
                invoices, payments, balances, inventory, cash, activityNotifications);
    }
    @Bean
    public GetAllReturnsUseCase getAllReturnsUseCase(
            ReturnResponseAssembler responses) {
        return new GetAllReturnsUseCase(returnRepository, responses);
    }
    @Bean
    public GetReturnByIdUseCase getReturnByIdUseCase(
            ReturnResponseAssembler responses) {
        return new GetReturnByIdUseCase(returnRepository, responses);
    }
    @Bean
    public DeleteReturnUseCase deleteReturnUseCase(
            ReturnInvoiceAdjustmentGateway adjustments,
            ReturnInvoiceQueryGateway invoices,
            ReturnInventoryGateway inventory,
            ReturnPaymentSettlementGateway payments,
            ReturnBalanceGateway balances,
            ActivityNotificationRecorder activityNotifications) {
        return new DeleteReturnUseCase(adjustments, returnRepository, invoices,
                inventory, payments, balances, activityNotifications);
    }
}
