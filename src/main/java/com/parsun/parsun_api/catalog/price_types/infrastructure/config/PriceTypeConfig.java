package com.parsun.parsun_api.catalog.price_types.infrastructure.config;

import com.parsun.parsun_api.platform.notifications.application.ports.in.ActivityNotificationRecorder;
import com.parsun.parsun_api.catalog.price_types.application.create.use_case.CreatePriceTypeUseCase;
import com.parsun.parsun_api.catalog.price_types.application.delete.use_case.DeletePriceTypeUseCase;
import com.parsun.parsun_api.catalog.price_types.application.queries.use_case.GetPriceTypeUseCase;
import com.parsun.parsun_api.catalog.price_types.application.queries.use_case.ListPriceTypesUseCase;
import com.parsun.parsun_api.catalog.price_types.application.update.use_case.UpdatePriceTypeUseCase;
import com.parsun.parsun_api.catalog.price_types.domain.repository.PriceTypeRepository;
import com.parsun.parsun_api.catalog.price_types.application.ports.out.PriceTypeUsageGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PriceTypeConfig {

    @Bean
    public CreatePriceTypeUseCase createPriceTypeUseCase(
            PriceTypeRepository priceTypeRepository,
            ActivityNotificationRecorder activityNotifications) {
        return new CreatePriceTypeUseCase(priceTypeRepository, activityNotifications);
    }

    @Bean
    public UpdatePriceTypeUseCase updatePriceTypeUseCase(
            PriceTypeRepository priceTypeRepository,
            com.parsun.parsun_api.shared.application.ports.in.ActiveTogglePermission access,
            ActivityNotificationRecorder activityNotifications) {
        return new UpdatePriceTypeUseCase(priceTypeRepository, access,
                activityNotifications);
    }

    @Bean
    public DeletePriceTypeUseCase deletePriceTypeUseCase(
            PriceTypeUsageGateway usageGateway,
            PriceTypeRepository priceTypeRepository,
            ActivityNotificationRecorder activityNotifications) {
        return new DeletePriceTypeUseCase(usageGateway, priceTypeRepository, activityNotifications);
    }

    @Bean
    public GetPriceTypeUseCase getPriceTypeUseCase(
            PriceTypeRepository priceTypeRepository,
            com.parsun.parsun_api.catalog.price_types.application.ports.out.PriceTypeUsageGateway usageGateway) {
        return new GetPriceTypeUseCase(priceTypeRepository, usageGateway);
    }

    @Bean
    public ListPriceTypesUseCase listPriceTypesUseCase(
            PriceTypeUsageGateway usageGateway,
            PriceTypeRepository priceTypeRepository) {
        return new ListPriceTypesUseCase(usageGateway, priceTypeRepository);
    }
}
