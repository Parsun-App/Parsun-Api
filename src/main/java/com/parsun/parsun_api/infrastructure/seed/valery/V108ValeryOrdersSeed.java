package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import org.springframework.stereotype.Component;

@Component
public class V108ValeryOrdersSeed implements SeedStep {

    private final ValeryDocumentsSeeder seeder;

    public V108ValeryOrdersSeed(ValeryDocumentsSeeder seeder) {
        this.seeder = seeder;
    }

    @Override
    public int version() {
        return 108;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        seeder.seedOrders();
    }
}
