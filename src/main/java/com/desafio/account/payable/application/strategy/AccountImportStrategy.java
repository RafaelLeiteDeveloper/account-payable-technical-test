package com.desafio.account.payable.application.strategy;

import com.desafio.account.payable.application.dto.request.AccountRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface AccountImportStrategy {
    List<AccountRequest> importAccounts(InputStream inputStream, String processId) throws IOException;
}
