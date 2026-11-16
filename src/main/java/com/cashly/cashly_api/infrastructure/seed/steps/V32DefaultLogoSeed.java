package com.cashly.cashly_api.infrastructure.seed.steps;

import com.cashly.cashly_api.infrastructure.seed.SeedStep;
import com.cashly.cashly_api.platform.company.application.ports.out.CompanyMediaStorage;
import com.cashly.cashly_api.platform.company.domain.entity.Company;
import com.cashly.cashly_api.platform.company.domain.repository.CompanyRepository;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class V32DefaultLogoSeed implements SeedStep {

    private static final String DEFAULT_LOGO = "/branding/default-logo.png";

    private final CompanyRepository companies;
    private final CompanyMediaStorage images;

    public V32DefaultLogoSeed(CompanyRepository companies,
            CompanyMediaStorage images) {
        this.companies = companies;
        this.images = images;
    }

    @Override
    public int version() {
        return 32;
    }

    @Override
    public void seed() {
        var company = companies.find();
        if (company.logoPath() != null && !company.logoPath().isBlank()) {
            return;
        }
        String logoPath = images.store(readDefaultLogo());
        var updated = new Company(company.id(), company.legalName(),
                company.tradeName(), company.taxId(), company.email(),
                company.phone(), company.website(), company.address(),
                logoPath, company.pdfLogoPath());
        companies.save(updated);
    }

    private byte[] readDefaultLogo() {
        try (var input = V32DefaultLogoSeed.class
                .getResourceAsStream(DEFAULT_LOGO)) {
            if (input == null) {
                throw new IllegalStateException(
                        "Default logo not found: " + DEFAULT_LOGO);
            }
            return input.readAllBytes();
        } catch (IOException exception) {
            throw new IllegalStateException("Cannot read default logo",
                    exception);
        }
    }
}
