package com.desafio.account.payable.infrastructure.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyFormatterUtil {

    private static final Locale BRAZILIAN_LOCALE = Locale.of("pt", "BR");

    private CurrencyFormatterUtil() { }

    public static String formatToBrazilianCurrency(BigDecimal value) {
        BigDecimal roundedAmount = value.setScale(2, BigDecimal.ROUND_HALF_UP);
        return "R$ " + roundedAmount.toString();
    }
}
