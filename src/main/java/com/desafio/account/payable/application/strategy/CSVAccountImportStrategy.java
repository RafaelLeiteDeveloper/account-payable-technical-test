package com.desafio.account.payable.application.strategy;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.infrastructure.util.CSVParserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CSVAccountImportStrategy implements AccountImportStrategy {

    private final CSVParserUtil csvParserUtil;

    @Override
    public List<AccountRequest> importAccounts(InputStream inputStream, String processId) throws IOException {
        return csvParserUtil.parseCSVToAccountRequests(inputStream, processId);
    }

}
