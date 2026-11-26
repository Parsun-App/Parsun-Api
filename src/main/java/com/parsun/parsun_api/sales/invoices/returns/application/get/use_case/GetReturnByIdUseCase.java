package com.parsun.parsun_api.sales.invoices.returns.application.get.use_case;

import com.parsun.parsun_api.sales.invoices.returns.application.dto.response.ReturnResponse;
import com.parsun.parsun_api.sales.invoices.returns.application.services.ReturnResponseAssembler;
import com.parsun.parsun_api.sales.invoices.returns.domain.entity.Return;
import com.parsun.parsun_api.sales.invoices.returns.domain.valueobjects.ReturnId;
import com.parsun.parsun_api.sales.invoices.returns.domain.repository.ReturnRepository;

import java.util.UUID;

public class GetReturnByIdUseCase {

    private final ReturnRepository returnRepository;
    private final ReturnResponseAssembler responses;

    public GetReturnByIdUseCase(ReturnRepository returnRepository,
            ReturnResponseAssembler responses) {
        this.returnRepository = returnRepository;
        this.responses = responses;
    }

    public ReturnResponse execute(UUID id) {
        Return return_ = returnRepository.findById(ReturnId.of(id))
                .orElseThrow(() -> new ReturnNotFoundException(id));

        return responses.toResponse(return_);
    }

    public static class ReturnNotFoundException extends RuntimeException {
        public ReturnNotFoundException(UUID id) {
            super("Return not found with id: " + id);
        }
    }
}
