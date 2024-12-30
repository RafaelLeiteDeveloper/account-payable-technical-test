package com.desafio.account.payable.application.service;

import com.desafio.account.payable.domain.model.FileMessage;
import java.io.IOException;

public interface AccountImportService {
    void importAccounts(FileMessage fileMessage, String processId) throws IOException;
    void verifyIdempotencyAndSaveAudit(String processId);
    void finalProcessImport(String processId);
}
