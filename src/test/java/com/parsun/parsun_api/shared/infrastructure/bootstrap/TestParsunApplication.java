package com.parsun.parsun_api.shared.infrastructure.bootstrap;

import com.parsun.parsun_api.shared.infrastructure.config.TestcontainersConfiguration;

import org.springframework.boot.SpringApplication;
import com.parsun.parsun_api.ParsunApplication;

public class TestParsunApplication {

    public static void main(String[] args) {
        SpringApplication.from(ParsunApplication::main)
                .with(TestcontainersConfiguration.class).run(args);
    }

}
