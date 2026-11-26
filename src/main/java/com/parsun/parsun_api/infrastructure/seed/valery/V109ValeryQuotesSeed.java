package com.parsun.parsun_api.infrastructure.seed.valery;

import com.parsun.parsun_api.infrastructure.seed.SeedSource;
import com.parsun.parsun_api.infrastructure.seed.SeedStep;
import org.springframework.stereotype.Component;

@Component
public class V109ValeryQuotesSeed implements SeedStep {

    private final ValeryDocumentsSeeder seeder;

    public V109ValeryQuotesSeed(ValeryDocumentsSeeder seeder) {
        this.seeder = seeder;
    }

    @Override
    public int version() {
        return 109;
    }

    @Override
    public SeedSource source() {
        return SeedSource.VALERY;
    }

    @Override
    public void seed() {
        seeder.seedQuotes();
    }
}
