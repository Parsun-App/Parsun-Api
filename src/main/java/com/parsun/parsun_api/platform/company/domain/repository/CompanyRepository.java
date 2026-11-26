package com.parsun.parsun_api.platform.company.domain.repository;
import com.parsun.parsun_api.platform.company.domain.entity.Company;
public interface CompanyRepository {
    Company find();
    Company lock();
    void createIfAbsent(Company company);
    void save(Company company);
}
