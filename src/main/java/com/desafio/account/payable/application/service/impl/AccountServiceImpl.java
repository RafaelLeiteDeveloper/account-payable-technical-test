package com.desafio.account.payable.application.service.impl;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.dto.response.AccountResponse;
import com.desafio.account.payable.application.mapper.AccountMapper;
import com.desafio.account.payable.application.service.AccountService;
import com.desafio.account.payable.domain.model.AccountModel;
import com.desafio.account.payable.domain.model.AccountStatus;
import com.desafio.account.payable.domain.repository.AccountRepository;
import com.desafio.account.payable.infrastructure.repository.AccountSpecification;
import com.desafio.account.payable.infrastructure.util.MonetaryUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponse findById(Long id) {
        log.info("Starting the getOrderById in AccountResponse ID: {}", id);

        AccountModel accountModel = this.accountRepository.findByIdOrError(id);

        log.info("End the getOrderById in AccountService accountModel: {}", accountModel.toString());

        return AccountMapper.toAccountResponse(accountModel);
    }

    @Override
    public String getTotalPaidInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating total paid between {} and {}", startDate, endDate);

        List<AccountModel> accounts = this.accountRepository.findAccountsByPaymentDateBetween(startDate, endDate);
        BigDecimal totalPaid = MonetaryUtil.getTotalPaid(accounts);
        String formattedTotalPaid = MonetaryUtil.formatToBrazilianCurrency(totalPaid);

        log.info("Total paid between {} and {}: {}", startDate, endDate, formattedTotalPaid);

        return formattedTotalPaid;
    }

    @Override
    public Page<AccountResponse> getAccounts(LocalDateTime dueDateStart, LocalDateTime dueDateEnd, String description, Pageable pageable) {
        log.info("Fetching accounts with filters: dueDateStart={}, dueDateEnd={}, description={}",
                dueDateStart, dueDateEnd, description);

        Specification<AccountModel> spec = Specification.where(AccountSpecification.hasDueDateStart(dueDateStart))
                .and(AccountSpecification.hasDueDateEnd(dueDateEnd))
                .and(AccountSpecification.hasDescription(description));

        Page<AccountModel> accountsPage = this.accountRepository.findAll(spec, pageable);

        log.info("Fetched {} accounts with filters", accountsPage.getTotalElements());

        return accountsPage.map(AccountMapper::toAccountResponse);
    }

    @Override
    @Transactional
    public void createAccount(AccountRequest accountRequest) {
        AccountModel accountModel = AccountMapper.toAccountModel(accountRequest);
        this.accountRepository.save(accountModel);
    }

    @Override
    @Transactional
    public void updateAccount(Long id, AccountRequest accountRequest) {
        log.info("Starting the updateAccount in AccountService for ID: {}", id);

        AccountModel accountModel = this.accountRepository.findByIdOrError(id);
        AccountMapper.updateAccountModel(accountRequest, accountModel);
        this.accountRepository.save(accountModel);

        log.info("End the updateAccount in AccountService for ID: {}", id);

    }

    @Override
    @Transactional
    public void updateAccountStatus(Long id, AccountStatus status) {
        log.info("Starting the updateAccountStatus in AccountService for ID: {} with status: {}", id, status);

        AccountModel accountModel = this.accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        verifyAndSetStatus(status,accountModel);

        this.accountRepository.save(accountModel);

        log.info("End the updateAccountStatus in AccountService for ID: {}", id);

    }

    private void verifyAndSetStatus(AccountStatus status, AccountModel accountModel ){

        if (!AccountStatus.isValidStatus(status.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status.getStatus());
        }

        accountModel.setStatus(status.getStatus());
    }

}
