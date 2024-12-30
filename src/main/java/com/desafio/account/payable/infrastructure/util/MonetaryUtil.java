package com.desafio.account.payable.infrastructure.util;

import com.desafio.account.payable.domain.model.AccountModel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
public class MonetaryUtil {

    public static String formatToBrazilianCurrency(BigDecimal value) {
        return "R$ " + value.setScale(2, RoundingMode.HALF_UP).toString().replace(".", ",");
    }

    public static BigDecimal getTotalPaid(List<AccountModel> accounts){
        return accounts.stream()
                .map(AccountModel::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
