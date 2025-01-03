package com.desafio.account.payable.application.service;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.mapper.AccountMapper;
import com.desafio.account.payable.application.strategy.AccountImportStrategyFactory;
import com.desafio.account.payable.domain.model.AccountModel;
import com.desafio.account.payable.domain.model.AuditImportModel;
import com.desafio.account.payable.domain.model.FileMessage;
import com.desafio.account.payable.domain.repository.AccountRepository;
import com.desafio.account.payable.application.strategy.AccountImportStrategy;
import com.desafio.account.payable.domain.repository.AuditImportRepository;
import com.desafio.account.payable.infrastructure.util.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountImportService {

    private final AccountRepository accountRepository;

    private final AccountImportStrategyFactory accountImportStrategyFactory;

    private final AuditImportRepository auditImportRepository;

    private final QueueService queueService;

    public void importAccounts(FileMessage fileMessage, String processId) throws IOException {
        log.info("Starting account import for processId: {}", processId);

        if(verifyIdempotencyAndSaveAudit(processId)) {
            AccountImportStrategy accountImportStrategy = accountImportStrategyFactory.getStrategy(fileMessage.getFileType());

            log.info("Import strategy for file type {} obtained.", fileMessage.getFileType());

            List<AccountRequest> accountRequests = accountImportStrategy.importAccounts(
                    FileUtil.convertByteArrayToInputStream(fileMessage.getFileContent()), processId);

            log.info("Number of accounts to import: {}", accountRequests.size());

            for (AccountRequest accountRequest : accountRequests) {
                try {
                    log.debug("Mapping account request: {}", accountRequest);
                    AccountModel accountModel = AccountMapper.toAccountModel(accountRequest);
                    log.debug("Saving account model: {}", accountModel);
                    accountRepository.save(accountModel);
                    log.info("Account saved successfully: {}", accountModel.getId());
                } catch (Exception e) {
                    log.error("Error saving account: {}. Sending message to DLQ", accountRequest, e);
                    queueService.sendToToQueueDlq(FileUtil.convertByteArrayToInputStream(fileMessage.getFileContent()), processId);
                }
            }

            finalProcessImport(processId);
        }
        log.info("Account import process completed for processId: {}", processId);
    }

    public boolean verifyIdempotencyAndSaveAudit(String processId) {
        log.info("Verifying idempotency for processId: {}", processId);
        if (auditImportRepository.findByIdProcess(processId).isEmpty()) {
            auditImportRepository.save(AuditImportModel.builder()
                    .startDate(LocalDateTime.now())
                    .idProcess(processId)
                    .build());

            log.info("Audit record created for processId: {}", processId);
            return true;
        }

        log.error("Duplicate processId detected: {}", processId);
        return false;
    }

    public void finalProcessImport(String processId) {
        log.info("Finalizing import process for processId: {}", processId);

        Optional<AuditImportModel> auditImportModelOptional = auditImportRepository.findByIdProcess(processId);

        auditImportModelOptional.ifPresent(auditImportModel -> {
            auditImportModel.setEndDate(LocalDateTime.now());
            auditImportRepository.save(auditImportModel);
            log.info("Import process finalized for processId: {}", auditImportModel.getIdProcess());
        });

        if (auditImportModelOptional.isEmpty()) {
            log.warn("No import process found for processId: {}", processId);
        }
    }


}
