package com.parsun.parsun_api.catalog.categories.application.queries.use_case;

import com.parsun.parsun_api.catalog.categories.application.dto.response.CategoryDetailResponse;
import com.parsun.parsun_api.catalog.categories.application.exceptions.CategoryNotFoundException;
import com.parsun.parsun_api.catalog.categories.application.mappers.CategoryMapper;
import com.parsun.parsun_api.catalog.categories.domain.entity.Category;
import com.parsun.parsun_api.catalog.categories.application.ports.out.CategoryUsageGateway;
import com.parsun.parsun_api.catalog.categories.domain.repository.CategoryRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public class GetCategoryUseCase {

    private final CategoryUsageGateway usageGateway;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public GetCategoryUseCase(CategoryUsageGateway usageGateway,
            CategoryRepository categoryRepository,
            CategoryMapper categoryMapper) {
        this.usageGateway = usageGateway;
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional(readOnly = true)
    public CategoryDetailResponse execute(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        long productCount = usageGateway.countActiveProductsByCategory(id);
        return categoryMapper.toDetailResponse(category, productCount);
    }
}
