package com.parsun.parsun_api.finance.treasury.cash_register.sessions.infrastructure.config;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.ports.in.ReturnCashRecording;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.services.ReturnCashRecordingService;
import com.parsun.parsun_api.finance.treasury.cash_register.sessions.application.movements.services.CashMovementService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
public class ReturnCashRecordingConfig {
    @Bean
    public ReturnCashRecording returnCashRecording(
            CashMovementService movements) {
        return new ReturnCashRecordingService(movements);
    }
}
