package com.desafio.account.payable.application.service;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.dto.response.AccountResponse;
import com.desafio.account.payable.application.mapper.AccountMapper;
import com.desafio.account.payable.domain.model.AccountModel;
import com.desafio.account.payable.domain.model.AccountStatus;
import com.desafio.account.payable.domain.repository.AccountRepository;
import com.desafio.account.payable.infrastructure.repository.AccountSpecification;
import com.desafio.account.payable.infrastructure.util.CurrencyFormatterUtil;
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
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse findById(Long id) {
        log.info("Starting the getOrderById in AccountResponse ID: {}", id);

        AccountModel accountModel = findOrError(id);

        log.info("End the getOrderById in AccountService accountModel: {}", accountModel.toString());

        return AccountMapper.toAccountResponse(accountModel);
    }

    public String getTotalPaidInPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Calculating total paid between {} and {}", startDate, endDate);

        List<AccountModel> accounts = accountRepository.findAccountsByPaymentDateBetween(startDate, endDate);
        BigDecimal totalPaid = getTotalPaid(accounts);
        String formattedTotalPaid = CurrencyFormatterUtil.formatToBrazilianCurrency(totalPaid);

        log.info("Total paid between {} and {}: {}", startDate, endDate, formattedTotalPaid);

        return formattedTotalPaid;
    }

    public Page<AccountResponse> getAccounts(LocalDateTime dueDateStart, LocalDateTime dueDateEnd, String description, Pageable pageable) {
        log.info("Fetching accounts with filters: dueDateStart={}, dueDateEnd={}, description={}",
                dueDateStart, dueDateEnd, description);

        Specification<AccountModel> spec = Specification.where(AccountSpecification.hasDueDateStart(dueDateStart))
                .and(AccountSpecification.hasDueDateEnd(dueDateEnd))
                .and(AccountSpecification.hasDescription(description));

        Page<AccountModel> accountsPage = accountRepository.findAll(spec, pageable);

        log.info("Fetched {} accounts with filters", accountsPage.getTotalElements());

        return accountsPage.map(AccountMapper::toAccountResponse);
    }

    @Transactional
    public void createAccount(AccountRequest accountRequest) {
        validateAccountStatus(accountRequest.getStatus());
        AccountModel accountModel = AccountMapper.toAccountModel(accountRequest);
        accountRepository.save(accountModel);
    }

    private void validateAccountStatus(AccountStatus status) {
        if (!AccountStatus.ACTIVE.equals(status)) {
            throw new IllegalArgumentException(String.format("Invalid status: '%s'. Only 'ACTIVE' is allowed when creating a new account.", status));
        }
    }

    @Transactional
    public void updateAccount(Long id, AccountRequest accountRequest) {
        log.info("Starting the updateAccount in AccountService for ID: {}", id);

        AccountModel accountModel = findOrError(id);

        AccountMapper.updateAccountModel(accountRequest, accountModel);

        accountRepository.save(accountModel);

        log.info("End the updateAccount in AccountService for ID: {}", id);

    }
    @Transactional
    public void updateAccountStatus(Long id, AccountStatus status) {
        log.info("Starting the updateAccountStatus in AccountService for ID: {} with status: {}", id, status);

        AccountModel accountModel = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        verifyAndSetStatus(status,accountModel);

        accountRepository.save(accountModel);

        log.info("End the updateAccountStatus in AccountService for ID: {}", id);

    }

    private void verifyAndSetStatus(AccountStatus status, AccountModel accountModel ){

        if (!AccountStatus.isValidStatus(status.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + status.getStatus());
        }

        accountModel.setStatus(status.getStatus());
    }

    public AccountModel findOrError(Long id){
        return accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    private BigDecimal getTotalPaid(List<AccountModel> accounts){
        return accounts.stream()
                .map(AccountModel::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
