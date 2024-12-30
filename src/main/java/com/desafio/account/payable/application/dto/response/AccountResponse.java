package com.desafio.account.payable.application.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AccountResponse {
    private Long id;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private String amount;
    private String description;
    private String status;
}
