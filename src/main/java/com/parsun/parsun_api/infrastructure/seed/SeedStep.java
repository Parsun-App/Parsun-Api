package com.parsun.parsun_api.infrastructure.seed;

public interface SeedStep {
    int version();
    void seed();

    default SeedSource source() {
        return SeedSource.DEMO;
    }
}
