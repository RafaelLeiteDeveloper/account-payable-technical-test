package com.desafio.account.payable.application.dto.request;

import com.desafio.account.payable.domain.model.AccountStatus;
import com.desafio.account.payable.interfaces.util.EnumValue;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotNull(message = "Due date must not be null")
    @FutureOrPresent(message = "Due date must be in the present or the future")
    private LocalDateTime dueDate;

    @PastOrPresent(message = "Payment date must be in the past or present")
    private LocalDateTime paymentDate;

    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.00", inclusive = true, message = "Amount must be greater than or equal to 0")
    @DecimalMax(value = "999999999999.99", inclusive = true, message = "Amount must be less than or equal to 999,999,999,999.99")
    private BigDecimal amount;

    @NotBlank(message = "Description must not be empty")
    @Size(max = 50, message = "Description must be no longer than 50 characters")
    private String description;

    @NotNull(message = "Status must not be null")
    @EnumValue(enumClass = AccountStatus.class, message = "Status must be one of the following: ACTIVE, INACTIVE, PENDING, PAID, CANCELLED")
    private AccountStatus status;
}
