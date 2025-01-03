package com.desafio.account.payable.infrastructure.repository;

import com.desafio.account.payable.domain.model.AccountModel;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AccountSpecification {

    public static Specification<AccountModel> hasDueDateStart(LocalDateTime dueDateStart) {
        return (root, query, criteriaBuilder) -> {
            if (dueDateStart == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), dueDateStart);
        };
    }

    public static Specification<AccountModel> hasDueDateEnd(LocalDateTime dueDateEnd) {
        return (root, query, criteriaBuilder) -> {
            if (dueDateEnd == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), dueDateEnd);
        };
    }

    public static Specification<AccountModel> hasDescription(String description) {
        return (root, query, criteriaBuilder) -> {
            if (description == null || description.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(root.get("description"), "%" + description + "%");
        };
    }
}

