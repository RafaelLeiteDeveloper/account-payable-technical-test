package com.desafio.account.payable.infrastructure.util;

import com.desafio.account.payable.application.dto.request.AccountRequest;
import com.desafio.account.payable.application.service.QueueService;
import com.desafio.account.payable.domain.model.AccountStatus;
import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CSVParserUtil {

    private static final String EXPECTED_HEADER = "dueDate,paymentDate,amount,description,status";

    private final QueueService queueServiceImpl;

    public List<AccountRequest> parseCSVToAccountRequests(InputStream inputStream, String processId) throws IOException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            List<String[]> rows = reader.readAll();

            if (rows.isEmpty() || !String.join(",", rows.get(0)).equals(EXPECTED_HEADER)) {
                throw new IllegalArgumentException("Invalid CSV header. Expected: " + EXPECTED_HEADER);
            }

            return rows.stream().skip(1)
                    .map(row -> AccountRequest.builder()
                            .dueDate(parseLocalDateTime(row[0]))
                            .paymentDate(parseLocalDateTime(row[1]))
                            .amount(parseBigDecimal(row[2]))
                            .description(row[3])
                            .status(parseAccountStatus(row[4]))
                            .build())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            this.queueServiceImpl.sendToToQueueDlq(inputStream, processId);
            throw new RuntimeException("Error parsing CSV file", e);
        }
    }

    private LocalDateTime parseLocalDateTime(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + value, e);
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format: " + value, e);
        }
    }

    private AccountStatus parseAccountStatus(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        try {
            return AccountStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + value, e);
        }
    }

}
