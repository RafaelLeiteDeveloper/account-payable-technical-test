package com.desafio.account.payable.application.service;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.dto.response.AccountResponse;
import com.desafio.account.payable.domain.model.AccountStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;

public interface AccountService {
    AccountResponse findById(Long id);
    String getTotalPaidInPeriod(LocalDateTime startDate, LocalDateTime endDate);
    Page<AccountResponse> getAccounts(LocalDateTime dueDateStart, LocalDateTime dueDateEnd, String description, Pageable pageable);
    void createAccount(AccountRequest accountRequest);
    void updateAccount(Long id, AccountRequest accountRequest);
    void updateAccountStatus(Long id, AccountStatus status);
}
