package com.desafio.account.payable.interfaces.controller;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.dto.response.AccountResponse;
import com.desafio.account.payable.application.dto.response.TotalPaidResponse;
import com.desafio.account.payable.application.service.AccountService;
import com.desafio.account.payable.domain.model.AccountStatus;
import com.desafio.account.payable.infrastructure.util.DateValidatorUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@SuppressWarnings("unused")
@RequestMapping("/v1/account")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getOrderById(@PathVariable("id") @Valid Long id){
        log.info("Starting the getOrderById in PaymentController ID: {}", id);
        AccountResponse accountResponse = this.accountService.findById(id);
        log.info("End the getOrderById in PaymentController ID: {}", id);
        return ResponseEntity.status(HttpStatus.OK).body(accountResponse);
    }

    @GetMapping("/totalPaid")
    public ResponseEntity<TotalPaidResponse> getTotalPaid(
            @RequestParam("startDate") @NotBlank(message = "The 'startDate' parameter is required") String startDateStr,
            @RequestParam("endDate") @NotBlank(message = "The 'endDate' parameter is required") String endDateStr) {

        log.info("Starting the getTotalPaid in PaymentController from {} to {}", startDateStr, endDateStr);

        LocalDateTime startDate = DateValidatorUtil.validateDate(startDateStr);
        LocalDateTime endDate = DateValidatorUtil.validateDate(endDateStr);

        String totalPaid = this.accountService.getTotalPaidInPeriod(startDate, endDate);

        log.info("End the getTotalPaid in PaymentController from {} to {}", startDateStr, endDateStr);

        return ResponseEntity.status(HttpStatus.OK).body(new TotalPaidResponse(totalPaid));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<AccountResponse>> getAccounts(
            @RequestParam(value = "dueDateStart", required = false) String dueDateStartStr,
            @RequestParam(value = "dueDateEnd", required = false) String dueDateEndStr,
            @RequestParam(value = "description", required = false) String description,
            Pageable pageable) {

        log.info("Starting the getAccounts in PaymentController with filters: dueDateStart={}, dueDateEnd={}, description={}",
                dueDateStartStr, dueDateEndStr, description);

        LocalDateTime dueDateStart = dueDateStartStr != null ? DateValidatorUtil.validateDate(dueDateStartStr) : null;
        LocalDateTime dueDateEnd = dueDateEndStr != null ? DateValidatorUtil.validateDate(dueDateEndStr) : null;

        Page<AccountResponse> accounts = accountService.getAccounts(dueDateStart, dueDateEnd, description, pageable);

        log.info("End the getAccounts in PaymentController with {} results", accounts.getTotalElements());

        return ResponseEntity.status(HttpStatus.OK).body(accounts);
    }

    @PostMapping
    public ResponseEntity<Void> createAccount(@RequestBody @Valid AccountRequest accountRequest) {
        log.info("Starting the createAccount in PaymentController with request: {}", accountRequest);

        accountService.createAccount(accountRequest);

        log.info("End the createAccount in PaymentController");

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAccount(
            @PathVariable("id") Long id,
            @RequestBody @Valid AccountRequest accountRequest) {

        log.info("Starting the updateAccount in PaymentController for ID: {}", id);

        this.accountService.updateAccount(id, accountRequest);

        log.info("End the updateAccount in PaymentController for ID: {}", id);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateAccountStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") AccountStatus status) {

        log.info("Starting the updateAccountStatus in PaymentController for ID: {} with status: {}", id, status);

        this.accountService.updateAccountStatus(id, status);

        log.info("End the updateAccountStatus in PaymentController for ID: {}", id);

        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

}
