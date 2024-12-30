package com.desafio.account.payable.domain.model;

import lombok.Getter;

@Getter
public enum AccountStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    PENDING("PENDING"),
    PAID("PAID"),
    CANCELLED("CANCELLED");

    private final String status;

    AccountStatus(String status) {
        this.status = status;
    }

    public static boolean isValidStatus(String status) {
        for (AccountStatus accountStatus : AccountStatus.values()) {
            if (accountStatus.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

}
