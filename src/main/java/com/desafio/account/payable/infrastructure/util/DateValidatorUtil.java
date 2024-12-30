package com.desafio.account.payable.infrastructure.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateValidatorUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTime validateDate(String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use the format: yyyy-MM-dd'T'HH:mm:ss");
        }
    }

}
