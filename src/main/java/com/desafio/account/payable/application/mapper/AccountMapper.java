package com.desafio.account.payable.application.mapper;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.domain.model.AccountModel;
import com.desafio.account.payable.application.dto.response.AccountResponse;
import com.desafio.account.payable.infrastructure.util.MonetaryUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

@Slf4j
public class AccountMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public static AccountResponse toAccountResponse(AccountModel accountModel) {
        log.info("Starting the ModelMapper AccountModel: {}", accountModel.toString());

        String formattedAmount = MonetaryUtil.formatToBrazilianCurrency(accountModel.getAmount());
        AccountResponse accountResponse = modelMapper.map(accountModel, AccountResponse.class);
        accountResponse.setAmount(formattedAmount);

        log.info("End the ModelMapper Model");
        return accountResponse;
    }

    public static AccountModel toAccountModel(AccountRequest accountRequest) {
        log.info("Starting the ModelMapper AccountRequest: {}", accountRequest.toString());

        AccountModel accountModel = modelMapper.map(accountRequest, AccountModel.class);

        log.info("End the ModelMapper Request");
        return accountModel;
    }

    public static void updateAccountModel(AccountRequest accountRequest, AccountModel accountModel) {
        log.info("Updating AccountModel with AccountRequest: {}", accountRequest.toString());

        modelMapper.map(accountRequest, accountModel);

        log.info("Updated AccountModel: {}", accountModel.toString());
    }
}
