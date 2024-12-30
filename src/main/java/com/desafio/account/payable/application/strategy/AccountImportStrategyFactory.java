package com.desafio.account.payable.application.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountImportStrategyFactory {

    @Autowired
    private CSVAccountImportStrategy csvAccountImportStrategy;

    public AccountImportStrategy getStrategy(String fileType) {
        if ("csv".equals(fileType.toLowerCase())) {
            return csvAccountImportStrategy;
        }
        throw new IllegalArgumentException("Unsupported file type: " + fileType);
    }

}